package internal

import (
	"fmt"
	"github.com/mholt/archiver"
	"github.com/planet-lia/planet-lia/cli"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"github.com/planet-lia/planet-lia/cli/internal/releases"
	"github.com/planet-lia/planet-lia/cli/pkg/advancedcopy"
	"github.com/planet-lia/planet-lia/cli/pkg/utils"
	"io/ioutil"
	"os"
	"os/exec"
	"path/filepath"
)

func FetchBot(gameName string, lang string, botPath string) {
	// Allows running deferred functions before exiting
	osExitStatus := -1
	defer func() {
		if osExitStatus != -1 {
			os.Exit(osExitStatus)
		}
	}()

	// Check if the bot with name already exists
	if exists, err := utils.DirectoryExists(botPath); err != nil {
		fmt.Fprintf(os.Stderr, "failed to check if directory with path '%s' exists\n%s\n", botPath, err)
		os.Exit(cli.Default)
	} else if exists {
		fmt.Fprintf(os.Stderr, "directory with path '%s' already exists, choose a different name\n", botPath)
		os.Exit(cli.Default)
	}

	botData, err := FindBotData(gameName, lang)
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get bot data for game '%s' and language '%s'\n%s\n",
			gameName, lang, err)
		os.Exit(cli.Default)
	}

	// Create temporary bot zip file
	botZipFile, err := ioutil.TempFile("", "planet-lia-bot-*.zip")
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to create tmp file: %s\n", err)
		osExitStatus = cli.OsCallFailed
		return
	}
	defer func() {
		botZipFile.Close()
		if err := os.Remove(botZipFile.Name()); err != nil {
			fmt.Fprintf(os.Stderr, "failed to remove bot zip '%s'\n%s\n", botZipFile.Name(), err)
		}
	}()

	// Download zip
	if err := utils.DownloadFile(botData.DownloadURL, botZipFile, 45); err != nil {
		fmt.Fprintf(os.Stderr, "failed to download bot zip from '%s' to '%s'\n%s\n",
			botData.DownloadURL, botZipFile.Name(), err)
		osExitStatus = cli.Default
		return
	}

	fmt.Println("Extracting...")

	// Create a directory where we will extract the bot
	tmpBotParentDir, err := ioutil.TempDir("", "planet-lia-bot-unzipped")
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to create tmp bot dir: %s", err)
		osExitStatus = cli.OsCallFailed
		return
	}
	defer func() {
		if err := os.RemoveAll(tmpBotParentDir); err != nil {
			fmt.Fprintf(os.Stderr, "failed to remove tmp bot parent dir '%s'\n%s\n", tmpBotParentDir, err)
		}
	}()

	//  Extract the bot
	if err := archiver.NewZip().Unarchive(botZipFile.Name(), tmpBotParentDir); err != nil {
		fmt.Fprintf(os.Stderr, "failed to extract bot with target %s\n%v\n", tmpBotParentDir, err)
		osExitStatus = cli.Default
		return
	}

	// Find bot directory name
	botDirName, err := getDirName(tmpBotParentDir)
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get bot dir name: %s\n", err)
		osExitStatus = cli.Default
		return
	}

	// Move bot dir and set new name
	tmpBotDir := filepath.Join(tmpBotParentDir, botDirName)

	if err := advancedcopy.Dir(tmpBotDir, botPath); err != nil {
		fmt.Fprintf(os.Stderr, "failed to copy bot dir from '%s' to '%s'\n%s\n", tmpBotDir, botPath, err)
		osExitStatus = cli.OsCallFailed
		return
	}

	fmt.Printf("Bot '%s' is ready!\n", botPath)
}

func FindBotData(gameName string, botLanguage string) (*releases.BotData, error) {
	game, err := GetGameData(gameName)
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to find the game with name '%s'\n%s\n", gameName, err)
		os.Exit(cli.Default)
	}

	for _, botData := range game.Bots {
		if botData.Language == botLanguage {
			return &botData, nil
		}
	}
	return nil, fmt.Errorf("bot for langugage '%s' is not provided for game '%s'", botLanguage, gameName)
}

func getDirName(parentDir string) (string, error) {
	files, err := ioutil.ReadDir(parentDir)
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to read files from dir '%s'", parentDir)
		return "", err
	}

	switch len(files) {
	case 1:
		return files[0].Name(), nil
	case 2:
		switch {
		case files[0].Name() == "__MACOSX":
			return files[1].Name(), nil
		case files[1].Name() == "__MACOSX":
			return files[0].Name(), nil
		}
	}

	return "", fmt.Errorf("there should be exactly 1 directory in parentDir"+
		"(on mac osx can also be __MACOSX. nFiles: %v", len(files))
}

func DeleteBot(botPath string) {
	// Check if the bot exists
	if exists, err := utils.DirectoryExists(botPath); err != nil {
		fmt.Fprintf(os.Stderr, "failed to check if directory with path '%s' exists\n%s\n", botPath, err)
		os.Exit(cli.Default)
	} else if !exists {
		fmt.Fprintf(os.Stderr, "directory with path '%s' does not exist\n", botPath)
		os.Exit(cli.Default)
	}

	// Check if directory contains a bot.json file
	pathToBotJson := filepath.Join(botPath, "bot.json")
	if _, err := os.Stat(pathToBotJson); err != nil {
		fmt.Fprintf(os.Stderr, "'%s' is not a bot as it does not contain a bot.json file\n", botPath)
		os.Exit(cli.Default)
	}

	if err := os.RemoveAll(botPath); err != nil {
		fmt.Fprintf(os.Stderr, "failed to delete bot '%s'\n", botPath)
		os.Exit(cli.Default)
	}

	fmt.Printf("Bot %s successfully deleted.\n", botPath)
}

func BuildBot(botPath string) {
	fmt.Printf("Building bot '%s'...\n", botPath)

	// Get build script path
	scriptPath := config.PathToBotScripts
	if config.OperatingSystem == "windows" {
		scriptPath = filepath.Join(scriptPath, config.BuildScriptWindowsName)
	} else {
		scriptPath = filepath.Join(scriptPath, config.BuildScriptUnixName)
	}

	var cmd *exec.Cmd
	if config.OperatingSystem == "windows" {
		cmd = exec.Command(".\\" + scriptPath)
	} else {
		cmd = exec.Command("/bin/bash", scriptPath)
	}
	cmd.Dir = botPath
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr

	if err := cmd.Run(); err != nil {
		fmt.Fprintf(os.Stderr, "failed to build bot '%s'\n", botPath)
		os.Exit(cli.BotBuildFailed)
	}

	fmt.Printf("Bot '%s' built successfully.\n", botPath)
}
