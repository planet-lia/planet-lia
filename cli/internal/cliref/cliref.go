package cliref

import (
	"encoding/json"
	"io/ioutil"
	"net/http"
	"time"
)

type CliRef struct {
	Cli   BaseData   `json:"cli"`
	Games []GameData `json:"games"`
}

type BaseData struct {
	Version string `json:"version"`
}

type GameData struct {
	Name        string    `json:"name"`
	Version     string    `json:"version"`
	DownloadURL string    `json:"downloadUrl"`
	Bots        []BotData `json:"bots"`
}

type BotData struct {
	Language    string `json:"language"`
	Version     string `json:"version"`
	DownloadURL string `json:"downloadUrl"`
}

func Get() (*CliRef, error) {
	url := "http://localhost:8000/cli-ref.json"

	client := http.Client{
		Timeout: time.Second * 2,
	}

	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}

	res, err := client.Do(req)
	if err != nil {
		return nil, err
	}

	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, err
	}

	cliRef := &CliRef{}
	err = json.Unmarshal(body, cliRef)
	if err != nil {
		return nil, err
	}

	return cliRef, nil
}
