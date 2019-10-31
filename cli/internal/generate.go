package internal

import (
	"bytes"
	"compress/gzip"
	"fmt"
	"github.com/planet-lia/planet-lia/cli"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"github.com/planet-lia/planet-lia/cli/internal/settings"
	uuid "github.com/satori/go.uuid"
	"io"
	"io/ioutil"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"strings"
	"time"
)

type MatchFlags struct {
	Teams               string
	ManualBots          []int
	Debug               bool
	Port                int
	ReplayPath          string
	ConfigPath          string
	BotListenerToken    string
	WindowToScreenRatio float32
}

type BotDetails struct {
	botPath string
	token   string
	debug   bool
	cmdRef  *CommandRef
}

func GenerateMatch(botPaths []string, gameFlags *MatchFlags) {
	configureReplayFilePath(gameFlags)
	configureGameConfig(gameFlags)
	configurePort(gameFlags)
	botsDetails := getBotsDetails(botPaths, gameFlags)

	// Create channel that will listen to results
	// from match generator and all of the bots
	result := make(chan error)

	generatorStarted := make(chan bool)

	// Run match-generator
	go func() {
		fmt.Printf("Running match generator.\n")
		cmdGenerator := &CommandRef{}
		err := runMatchGenerator(generatorStarted, cmdGenerator, gameFlags, botsDetails)
		cmdGenerator.cmd = nil
		result <- err
	}()

	// Wait until match generator has started
	<-generatorStarted

	generatorFinished := false

	// Run bots
	for _, botDetails := range botsDetails {
		if !botDetails.debug {
			go func(cmdBot *CommandRef, botDir, token string) {
				fmt.Printf("Running bot %s.\n", botDir)
				err := runBot(cmdBot, botDir, token, gameFlags.Port)
				if err != nil {
					if !generatorFinished {
						fmt.Fprintf(os.Stderr, "running bot %s failed %s\n", botDir, err)
					}
				}
				cmdBot.cmd = nil
			}(botDetails.cmdRef, botDetails.botPath, botDetails.token)
		}
	}

	// Wait for match generator to finish
	err := <-result
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to generate game, match generator error: %s\n", err)
		os.Exit(cli.Default)
	}

	generatorFinished = true

	// Attempt to kill the process to prevent daemons
	for _, botDetails := range botsDetails {
		killProcess(botDetails.cmdRef)
	}

	gzipReplayFile(gameFlags.ReplayPath)
}

func getBotsDetails(botDirs []string, gameFlags *MatchFlags) []BotDetails {
	var botsDetails []BotDetails

	for i, botDir := range botDirs {
		debug := contains(gameFlags.ManualBots, i)
		token := getBotToken(debug)
		botDetails := BotDetails{botDir, token, debug, &CommandRef{}}
		botsDetails = append(botsDetails, botDetails)
	}

	return botsDetails
}

func configureGameConfig(gameFlags *MatchFlags) {
	// If config path is provided make it absolute
	if gameFlags.ConfigPath != "" {
		var err error
		gameFlags.ConfigPath, err = filepath.Abs(gameFlags.ConfigPath)
		if err != nil {
			fmt.Fprintf(os.Stderr, "failed to make config path '%s' absolute: %s\n", gameFlags.ConfigPath, err)
		}
	}
}

func configurePort(gameFlags *MatchFlags) {
	if gameFlags.Port == 0 {
		gameFlags.Port = config.DefaultMatchPort
		// TODO find a port that is free
	}
}

func configureReplayFilePath(gameFlags *MatchFlags) {
	// If the replay file was not provided
	if gameFlags.ReplayPath == "" {
		path := filepath.Join(config.PathToReplayFiles, settings.Lia.SelectedGame)
		if err := os.MkdirAll(path, os.ModePerm); err != nil {
			fmt.Fprintf(os.Stderr, "failed to create directories for path '%s'\n", path)
		}
		//"2006-01-02T15:04:05Z07:00"
		fileName := time.Now().Format("2006-01-02T15-04-05") + ".json.gz"
		gameFlags.ReplayPath = filepath.Join(path, fileName)
	} else {
		absPath, err := filepath.Abs(gameFlags.ReplayPath)
		if err != nil {
			fmt.Fprintf(os.Stderr, "failed to create an aboslute path from path '%s'\n", gameFlags.ReplayPath)
		} else {
			gameFlags.ReplayPath = absPath
		}
	}
}

func gzipReplayFile(replayPath string) {
	// Read replay file
	replayData, err := ioutil.ReadFile(replayPath)
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to read the replay file for path '%s'\n%s\n", replayPath, err)
	}

	// Open a file for writing.
	outFile, err := os.Create(replayPath)
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to create and truncate the replay file for path '%s'\n%s\n", replayPath, err)
		return
	}

	// Create gzip writer.
	w := gzip.NewWriter(outFile)

	// Write bytes in compressed form to the file.
	if _, err := w.Write(replayData); err != nil {
		fmt.Fprintf(os.Stderr, "failed to gzip replay to path '%s'\n%s\n", replayPath, err)
	}

	// Close the file.
	if err := w.Close(); err != nil {
		fmt.Fprintf(os.Stderr, "failed to close replay file '%s'\n%s\n", replayPath, err)
	}
}

func parseBotName(botDir string) string {
	if config.OperatingSystem == "windows" {
		split := strings.Split(botDir, "\\")
		return split[len(split)-1]
	} else {
		split := strings.Split(botDir, "/")
		return split[len(split)-1]
	}
}

func contains(slice []int, e int) bool {
	for _, e2 := range slice {
		if e == e2 {
			return true
		}
	}
	return false
}

func getBotToken(debug bool) string {
	if debug {
		return "_"
	}
	return generateUuid()
}

func killProcess(cmdRef *CommandRef) {
	if cmdRef.cmd != nil {
		if err := cmdRef.cmd.Process.Kill(); err != nil {
			// Ignore, no valuable information
		}
	}
}

type CommandRef struct {
	cmd *exec.Cmd
}

func runBot(cmdRef *CommandRef, botPath, token string, port int) error {
	scriptPath := config.PathToBotScripts
	if config.OperatingSystem == "windows" {
		scriptPath = filepath.Join(scriptPath, config.RunScriptWindowsName)
	} else {
		scriptPath = filepath.Join(scriptPath, config.RunScriptUnixName)
	}

	var cmd *exec.Cmd
	if config.OperatingSystem == "windows" {
		cmd = exec.Command(".\\"+scriptPath, strconv.Itoa(port), token)
	} else {
		cmd = exec.Command("/bin/bash", scriptPath, strconv.Itoa(port), token)
	}

	cmdRef.cmd = cmd
	cmd.Dir = botPath
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr

	err := cmd.Run()
	if err != nil {
		return err
	}

	return nil
}

func runMatchGenerator(started chan bool, cmdRef *CommandRef, gameFlags *MatchFlags, botsDetails []BotDetails) error {
	pathToMatchGenerator := filepath.Join(config.PathToGames, settings.Lia.SelectedGame)

	cmd := exec.Command(
		"java",
		"-jar", filepath.Join(pathToMatchGenerator, "match-generator.jar"),
		"-p", fmt.Sprint(gameFlags.Port),
		"-r", gameFlags.ReplayPath,
	)
	cmdRef.cmd = cmd

	// Append optional flags
	if gameFlags.Debug {
		cmd.Args = append(cmd.Args, "-d")
	}
	if len(gameFlags.ConfigPath) > 0 {
		cmd.Args = append(cmd.Args, "-c", gameFlags.ConfigPath)
	}
	if len(gameFlags.Teams) > 0 {
		cmd.Args = append(cmd.Args, "-t", gameFlags.Teams)
	}
	if len(gameFlags.BotListenerToken) > 0 {
		cmd.Args = append(cmd.Args, "--bot-listener-token", gameFlags.BotListenerToken)
	}
	if gameFlags.WindowToScreenRatio != 0 {
		cmd.Args = append(cmd.Args, "-w", fmt.Sprintf("%f", gameFlags.WindowToScreenRatio))
	}

	cmd.Dir = pathToMatchGenerator

	// Append bots to the command
	for _, botDetails := range botsDetails {
		botName := parseBotName(botDetails.botPath) // Only display bot name and not full path
		cmd.Args = append(cmd.Args, botName, botDetails.token, "{}")
	}

	// Get pipes for stdout and stderr
	stdoutIn, err := cmd.StdoutPipe()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to create stdout pipe for match generator\n")
		return err
	}
	stderrIn, err := cmd.StderrPipe()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to create stdin pipe for match generator\n")
		return err
	}
	// Create multi writer that will pass result to stdout, stderr and buffers
	var stdoutBuf, stderrBuf bytes.Buffer
	stdout := io.MultiWriter(os.Stdout, &stdoutBuf)
	stderr := io.MultiWriter(os.Stderr, &stderrBuf)

	// Set the data flow from command to writers
	var errStdout, errStderr error
	go func() {
		_, errStdout = io.Copy(stdout, stdoutIn)
	}()

	go func() {
		_, errStderr = io.Copy(stderr, stderrIn)
	}()

	// Send true to started channel when match generator outputs something
	// (means that websocket server is prepared)
	go func() {
		for {
			if stdoutBuf.Len() > 0 || stderrBuf.Len() > 0 {
				started <- true
				return
			}
			time.Sleep(time.Millisecond * 20)
		}
	}()

	// Run match generator
	if err := cmd.Run(); err != nil {
		fmt.Fprintf(os.Stderr, "match generator failed\n")
		return err
	}

	// Copy replay file to the required destination

	if errStdout != nil {
		fmt.Fprintf(os.Stderr, "failed to capture stdout\n")
		started <- false
		return errStdout
	}
	if errStderr != nil {
		fmt.Fprintf(os.Stderr, "failed to capture stderr\n")
		started <- false
		return errStderr
	}

	return nil
}

func generateUuid() string {
	return uuid.NewV4().String()
}
