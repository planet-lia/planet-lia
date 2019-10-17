package game

import (
	"encoding/base64"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	"io/ioutil"
	"os"
	"path/filepath"
	"regexp"
)

func LiaSdkDir() string {
	return filepath.Dir(viper.GetString("lia-sdk"))
}

func GamesDir() string {
	return filepath.Join(LiaSdkDir(), "games")
}

func GameBotsDir(gameName string) string {
	return filepath.Join(GamesDir(), gameName, "bots")
}

// Checks if the game exists in the games directory.
func VerifyGameExists(gameName string) bool {
	exists, err := dirExists(filepath.Join(GamesDir(), gameName))
	if err != nil {
		logrus.Fatal(err)
	}

	return exists
}

// Checks if the bots dir has the appropriate combination of bots and languages.
func VerifyGameBots(gameName string) (noBots int, err error) {
	if !VerifyGameExists(gameName) {
		return 0, errors.New("game does not exist")
	}

	fi, err := ioutil.ReadDir(GameBotsDir(gameName))
	if err != nil {
		return 0, errors.Wrap(err, "failed to read game bot's dir")
	}

	botDirs := make([]string, 0)
	for _, f := range fi {
		if f.IsDir() {
			botDirs = append(botDirs, f.Name())
		}
	}

	noLanguages := 0 // according to the number of '*_bot1' directories
	langRegex, err := regexp.Compile(".*_bot1")
	if err != nil {
		logrus.Panic("Failed to compile language regex for VerifyGameBots", err)
	}

	for _, dir := range botDirs {
		if langRegex.MatchString(dir) {
			noLanguages++
		}
	}

	if noLanguages == 0 {
		return 0, errors.New("no bot languages detected")
	}

	noBots = len(botDirs) / noLanguages
	if noBots*noLanguages != len(botDirs) {
		return 0, errors.New("number of languages and bots mismatch")
	}

	return noBots, nil
}

func GetReplayFile(replayFilePath string) ([]byte, error) {
	replay, err := ioutil.ReadFile(replayFilePath)
	if err != nil {
		return []byte{}, errors.Wrap(err, "failed to open replay file")
	}

	return replay, nil
}

func GetReplayFileBase64Encoded(replayFilePath string) (string, error) {
	replay, err := GetReplayFile(replayFilePath)
	if err != nil {
		return "", err
	}

	return base64.StdEncoding.EncodeToString(replay), nil
}

func dirExists(path string) (bool, error) {
	// https://stackoverflow.com/a/10510783
	_, err := os.Stat(path)
	if err == nil {
		return true, nil
	}
	if os.IsNotExist(err) {
		return false, nil
	}
	return true, err
}
