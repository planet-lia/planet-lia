package tests

//
//import (
//	"fmt"
//	"github.com/planet-lia/planet-lia/cli"
//	"github.com/planet-lia/planet-lia/cli/internal"
//	"github.com/planet-lia/planet-lia/cli/internal/config"
//	"os"
//	"path/filepath"
//	"runtime"
//	"strconv"
//	"strings"
//	"testing"
//)
//
//// Add custom replayViewerWidth for testing
//var replayViewerWidth string
//
//func TestFull(t *testing.T) {
//	supportedLanguages := getSupportedLanguages()
//
//	// Run actual tests
//	for i, lang := range supportedLanguages {
//		if os.Getenv("RUN_FUNC") == strconv.Itoa(i) {
//			SetupTmpConfigPaths()
//			defer CleanupTmpFiles()
//			config.Setup()
//
//			botName := "birko"
//			pathToReplay := filepath.Join(config.PathToBots, "replay.lia")
//
//			// Fetch bot
//			internal.FetchBotByLanguage(lang, botName)
//
//			// Play
//			gameFlags := &internal.GameFlags{
//				GameSeed:   1,
//				MapSeed:    1,
//				Port:       8887,
//				ReplayPath: pathToReplay,
//			}
//			internal.Play(botName, botName, gameFlags, false, replayViewerWidth)
//
//			// Check if replay was created and is not empty
//			fi, err := os.Stat(pathToReplay)
//			if err != nil {
//				t.Fatal(err)
//			}
//			if fi.Size() <= 0 {
//				t.Fatalf("replay file is empty")
//			}
//
//			return
//		}
//	}
//
//	// Run test and check exit status
//	for i, lang := range supportedLanguages {
//		fmt.Printf("testing language %s...\n", lang)
//		output, exitStatus := GetCmdStatus("TestFull", i, true)
//		if exitStatus != lia_SDK.OK {
//			t.Logf("full test for language %s failed\n", lang)
//			t.Logf("%s", output)
//			t.Fatalf("exit status is %v but should be %v", exitStatus, lia_SDK.OK)
//		}
//	}
//}
//
//func getSupportedLanguages() []string {
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
//	pathToConfig := filepath.Join(wd[:index], "assets", "cli-config.json")
//
//	err = config.SetConfig(pathToConfig)
//	if err != nil {
//		panic(err)
//	}
//
//	var supportedLanguages []string
//	for _, langData := range config.Cfg.Languages {
//		supportedLanguages = append(supportedLanguages, langData.Name)
//	}
//
//	// CleanupUnix!
//	config.Cfg = nil
//
//	return supportedLanguages
//}
