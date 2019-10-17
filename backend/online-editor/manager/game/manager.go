package game

import (
	"bytes"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	"path/filepath"
	"time"
)

func Start(m Match, bots []Bot, shutdown chan bool) {
	if !VerifyGameExists(m.GameName) {
		logrus.WithFields(logrus.Fields{"gameName": m.GameName, "gameDir": GamesDir()}).Fatal("Game does not exist in gameDir")
	}

	noGameBots, err := VerifyGameBots(m.GameName)
	if err != nil {
		logrus.WithFields(logrus.Fields{"gameName": m.GameName, "error": err}).Fatal("Game bot verification failed")
	}

	if noGameBots != len(bots) {
		logrus.WithFields(logrus.Fields{"noManagerBots": len(bots), "noGameBots": noGameBots}).
			Fatal("Number of manager bots and game bots does not match")
	}

	jwtHeader := make(map[string]string)
	jwtHeader["Authorization"] = "Bearer " + viper.GetString("jwt")
	if err := AllBotsFetchSourceCode(&bots, jwtHeader); err != nil {
		logrus.WithFields(logrus.Fields{"error": err}).Fatal("Failed to fetch bot source code")
	}

	if err := AllBotsFetchBJ(&bots); err != nil {
		logrus.WithFields(logrus.Fields{"error": err}).Fatal("Failed to fetch bot json file")
	}

	if err := AllBotsInjectSource(&bots); err != nil {
		logrus.WithFields(logrus.Fields{"error": err}).Fatal("Failed to inject source code into bot")
	}

	syncShutdown := make(chan bool)
	matchGenerationShutdown := make(chan bool)
	isShutdown := false
	go func() {
		<-shutdown
		isShutdown = true
		syncShutdown <- true
		matchGenerationShutdown <- true
	}()

	var logBuffer bytes.Buffer
	go StateSync(500*time.Millisecond, &logBuffer, syncShutdown, m)

	replayFile := filepath.Join(LiaSdkDir(), "replay.lia")

	if err := GenerateMatch(m, bots, replayFile, &logBuffer, matchGenerationShutdown); err != nil {
		syncShutdown <- true
		if err := Update(m, StatusFailure, logBuffer.String(), ""); err != nil {
			logrus.WithField("error", err).Error("Failed to update state on backend-core to failrue")
		}
		logrus.WithFields(logrus.Fields{"error": err}).Fatal("Failed to generate match")
	}

	if isShutdown {
		logrus.Info("Exiting Online Editor manager due to shutdown")
		return
	}

	syncShutdown <- true
	replayB64, err := GetReplayFileBase64Encoded(replayFile + ".json")
	if err != nil {
		logrus.WithField("error", err).Fatal("Failed to get replay file")
	}

	if err := Update(m, StatusSuccess, logBuffer.String(), replayB64); err != nil {
		logrus.WithField("error", err).Fatal("Failed to update state on backend-core to success")
	}

	logrus.Info("Exiting Online Editor manager")
}
