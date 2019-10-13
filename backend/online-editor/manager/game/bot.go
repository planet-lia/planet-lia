package game

import (
	"encoding/json"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"io/ioutil"
	"net/http"
	"os"
	"path/filepath"
)

type Bot struct {
	Id string
	Language string
	Source []byte
	SourceUrl string
	Path string  // Path to Bot's directory
	BJ BotJson  // 😜
}

type Match struct {
	GameName string
	MatchId string
}

type BotJson struct {
	Language string `json:"language"`
	MainFile string `json:"mainFile"`
}

func (b *Bot) FetchSourceFromUrl(headers map[string]string) error {
	logrus.WithFields(logrus.Fields{"botId": b.Id, "url": b.SourceUrl}).Debug("Fetching bot source code")

	req, err := http.NewRequest(http.MethodGet, b.SourceUrl, nil)
	if err != nil {
		return errors.Wrap(err, "failed to create network request")
	}

	for k, v := range headers {
		req.Header.Add(k, v)
	}

	client := http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return errors.Wrap(err, "failed to perform network request")
	}

	if resp.StatusCode != http.StatusOK {
		return errors.New("server replied with HTTP status code: " + resp.Status)
	}

	d, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return errors.Wrap(err, "failed to read entire response buffer")
	}

	b.Source = d

	logrus.WithFields(logrus.Fields{"botId": b.Id, "url": b.SourceUrl}).Debug("Successfully fetched bot source code")
	return nil
}

func (b *Bot) GetBotJson() error {
	logrus.WithFields(logrus.Fields{"botPath": b.Path}).Debug("Getting bot json")

	jsonFilePath := filepath.Join(b.Path, "bot.json")

	botJsonRaw, err := ioutil.ReadFile(jsonFilePath)
	if err != nil {
		return errors.Wrap(err, "failed to read bot join file")
	}

	bj := BotJson{}

	err = json.Unmarshal(botJsonRaw, &bj)
	if err != nil {
		return errors.Wrap(err, "failed to unmarshal bot json")
	}

	b.BJ = bj
	return nil
}

func (b *Bot) InjectSourceCode() error {
	mainBotFilePath := filepath.Join(b.Path, b.BJ.MainFile)
	logrus.WithFields(logrus.Fields{"botPath": b.Path, "mainBotFile": mainBotFilePath}).Info("Injecting source code into bot")


	file, err := os.Create(mainBotFilePath)
	defer file.Close()
	if err != nil {
		return errors.Wrap(err, "failed to create main bot file")
	}

	_, err = file.Write(b.Source)
	if err != nil {
		return errors.Wrap(err, "failed to write source to main bot file")
	}

	return nil
}

func (b Bot) StupidLiaSDKPath(gameName string) string {
	return filepath.Join("games", gameName, "bots", b.Id)
}

func AllBotsFetchSourceCode(bots *[]Bot, headers map[string]string) error {
	for i, _ := range *bots {
		if err := (*bots)[i].FetchSourceFromUrl(headers); err != nil {
			return errors.Wrap(err, "failed to fetch bot source")
		}
	}
	return nil
}

func AllBotsFetchBJ(bots *[]Bot) error {
	for i, _ := range *bots {
		if err := (*bots)[i].GetBotJson(); err != nil {
			return errors.Wrap(err, "failed to fetch bot json")
		}
	}
	return nil
}

func AllBotsInjectSource(bots *[]Bot) error {
	for i, _ := range *bots {
		if err := (*bots)[i].InjectSourceCode(); err != nil {
			return errors.Wrap(err, "failed to inject source code into bot")
		}
	}
	return nil
}