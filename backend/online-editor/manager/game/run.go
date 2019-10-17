package game

import (
	"bytes"
	"encoding/json"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	"io"
	"net/http"
	"os"
	"os/exec"
	"path"
	"time"
)

const (
	StatusGenerating = "generating"
	StatusSuccess    = "success"
	StatusFailure    = "failure"
)

func GenerateMatch(m Match, bots []Bot, replayFilePath string, output io.Writer, shutdown chan bool) error {
	logrus.Info("Generating match...")

	cmd := exec.Command(viper.GetString("lia-sdk"))

	cmd.Args = append(cmd.Args, "generate")

	for _, bot := range bots {
		cmd.Args = append(cmd.Args, bot.StupidLiaSDKPath(m.GameName))
	}

	cmd.Args = append(cmd.Args, "-r")
	cmd.Args = append(cmd.Args, replayFilePath)

	cmd.Stdout = output
	cmd.Stderr = output

	if err := cmd.Start(); err != nil {
		logrus.WithField("error", err).Fatal("Failed to start lia-sdk")
	}

	done := make(chan error, 1)
	go func() {
		done <- cmd.Wait()
	}()

	select {
	case <-shutdown:
		logrus.Info("Killing lia-sdk")
		if err := cmd.Process.Kill(); err != nil {
			logrus.WithField("error", err).Error("Failed to kill lia-sdk")
		}
	case err := <-done:
		if err != nil {
			return errors.Wrap(err, "failed to run lia")
		}
	}

	return nil
}

func StateSync(interval time.Duration, logBuffer *bytes.Buffer, shutdown chan bool, m Match) {
	ticker := time.NewTicker(interval)

	for {
		select {
		case <-shutdown:
			logrus.Info("Shutting down state sync")
			ticker.Stop()
			return
		case <-ticker.C:
			if err := Update(m, StatusGenerating, logBuffer.String(), ""); err != nil {
				logrus.WithField("error", err).Error("Failed to update state at backend-core")
			}
		}
	}
}

func Update(m Match, status, log, replay string) error {
	logrus.Info("Triggering state update")

	statusEndpoint := path.Join("/internal/online-editor/match/", m.MatchId, "/state")
	statusEndpoint = viper.GetString("root-backend-endpoint") + statusEndpoint

	type body struct {
		Status string `json:"status"`
		Log    string `json:"log"`
		Replay string `json:"replay"`
	}

	b := body{
		status,
		log,
		replay,
	}

	bodyRaw, err := json.Marshal(&b)
	if err != nil {
		return errors.Wrap(err, "failed to construct request body")
	}

	req, err := http.NewRequest(http.MethodPost, statusEndpoint, bytes.NewBuffer(bodyRaw))
	if err != nil {
		return errors.Wrap(err, "failed to create network request")
	}

	req.Header.Add("Authorization", "Bearer "+viper.GetString("jwt"))
	req.Header.Add("Content-Type", "application/json")

	client := http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return errors.Wrap(err, "failed to perform network request")
	}

	if resp.StatusCode != http.StatusOK {
		return errors.New("HTTP response status code: " + resp.Status)
	}

	if resp.StatusCode == http.StatusNoContent {
		logrus.Warning("Match has expired, quitting now...")
		os.Exit(1)
		return nil
	}

	return nil
}
