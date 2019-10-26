package tests

//
//import (
//	"encoding/json"
//	"fmt"
//	"github.com/planet-lia/planet-lia/cli/internal/config"
//	"github.com/planet-lia/planet-lia/cli/pkg/advancedcopy"
//	"io"
//	"io/ioutil"
//	"os"
//	"os/exec"
//	"path/filepath"
//	"runtime"
//	"strconv"
//	"strings"
//	"syscall"
//)
//
//func GetCmdStatus(funcName string, envValue int, printToStdout bool) (string, int) {
//	cmd := exec.Command(os.Args[0], "-test.run="+funcName)
//	cmd.Env = append(os.Environ(), "RUN_FUNC="+strconv.Itoa(envValue))
//
//	var output []byte
//	var err error
//	if printToStdout {
//		cmd.Stdout = os.Stdout
//		cmd.Stderr = os.Stderr
//		err = cmd.Run()
//	} else {
//		output, err = cmd.CombinedOutput()
//	}
//	if e, ok := err.(*exec.ExitError); ok && !e.Success() {
//		if status, ok := e.Sys().(syscall.WaitStatus); ok {
//			return string(output), status.ExitStatus()
//		}
//	}
//	return string(output), 0
//}
//
//func SetupTmpConfigPaths() {
//	// Set tmp path to bots
//	var err error
//	config.PathToBots, err = ioutil.TempDir("", "")
//	if err != nil {
//		panic(err)
//	}
//
//	// Copy data to tmp path to bots
//	wd, err := os.Getwd()
//	if err != nil {
//		panic(err)
//	}
//	var index int
//	if runtime.GOOS == "windows" {
//		index = strings.LastIndex(wd, "\\")
//	} else {
//		index = strings.LastIndex(wd, "/")
//	}
//	pathToData := filepath.Join(wd[:index], "assets")
//	pathToTmpData := filepath.Join(config.PathToBots, "data")
//
//	if err := advancedcopy.Dir(pathToData, pathToTmpData); err != nil {
//		msg := fmt.Sprintf("failed to advancedcopy data to executable path %s", err)
//		panic(msg)
//	}
//
//	pathToGameConfig := filepath.Join(pathToTmpData, "game-config.json")
//	changeGameDurationInConfig(pathToGameConfig)
//}
//
//func changeGameDurationInConfig(path string) {
//	// Read config file
//	b, err := ioutil.ReadFile(path)
//	if err != nil {
//		panic(err)
//	}
//
//	jsonMap := make(map[string]interface{})
//	err = json.Unmarshal(b, &jsonMap)
//	if err != nil {
//		panic(err)
//	}
//
//	jsonMap["gameDetails"].(map[string]interface{})["gameDuration"] = "10"
//
//	jsonBytes, err := json.Marshal(jsonMap)
//	if err != nil {
//		panic(err)
//	}
//
//	if err := ioutil.WriteFile(path, jsonBytes, 0644); err != nil {
//		panic(err)
//	}
//
//}
//
//func CleanupTmpFiles() {
//	err := os.RemoveAll(config.PathToBots)
//	if err != nil {
//		panic(err)
//	}
//}
//
//func IsEmpty(name string) (bool, error) {
//	f, err := os.Open(name)
//	if err != nil {
//		return false, err
//	}
//	defer f.Close()
//
//	_, err = f.Readdirnames(1) // Or f.Readdir(1)
//	if err == io.EOF {
//		return true, nil
//	}
//	return false, err // Either not empty or error, suits both cases
//}
