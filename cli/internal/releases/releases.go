package releases

import (
	"encoding/json"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"io/ioutil"
	"net/http"
	"time"
)

type Releases struct {
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

func Get() (*Releases, error) {
	url := config.ReleasesUrl

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

	releases := &Releases{}
	err = json.Unmarshal(body, releases)
	if err != nil {
		return nil, err
	}

	return releases, nil
}
