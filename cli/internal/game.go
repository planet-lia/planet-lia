package internal

import (
	"encoding/json"
	"fmt"
	"github.com/hashicorp/go-version"
	"github.com/mholt/archiver"
	"github.com/planet-lia/planet-lia/cli"
	"github.com/planet-lia/planet-lia/cli/internal/cliref"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"io"
	"io/ioutil"
	"net/http"
	"os"
	"path/filepath"
)

func GameDownload(gameName string) {
	ref, err := cliref.Get()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get CliRef: %s\n", err)
		os.Exit(cli.CliRefFailed)
	}

	game, err := findGame(gameName, ref.Games)
	if err != nil {
		fmt.Fprintf(os.Stderr, err.Error())
		os.Exit(cli.Default)
	}

	if err := download(*game); err != nil {
		fmt.Fprintf(os.Stderr, "game download failed: %s\n", err)
		os.Exit(cli.GameDownloadFailed)
	}
}

func findGame(gameName string, games []cliref.GameData) (*cliref.GameData, error) {
	for _, game := range games {
		if game.Name == gameName {
			return &game, nil
		}
	}
	return nil, fmt.Errorf("game %s not found, run `./lia game list` to see supported games", gameName)
}

func download(game cliref.GameData) error {
	fmt.Printf("Downloading game: %s\n", game.Name)

	// Create games directory where executable is located
	if err := os.MkdirAll(config.PathToGames, os.ModePerm); err != nil {
		return err
	}

	pathToGameZip := filepath.Join(config.PathToGames, game.Name+".zip")
	pathToGame := filepath.Join(config.PathToGames, game.Name)

	resp, err := http.Get(game.DownloadURL)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	// Create the zip file
	out, err := os.Create(pathToGameZip)
	if err != nil {
		return err
	}
	defer func() {
		out.Close()
		if err := os.Remove(pathToGameZip); err != nil {
			fmt.Fprintf(os.Stderr, "failed to remove game zip %s\n", pathToGameZip)
		}
	}()

	// Write the body to file
	_, err = io.Copy(out, resp.Body)

	// Remove old game directory if it exists
	if err := os.RemoveAll(pathToGame); err != nil {
		return err
	}

	// Extract the zip
	if err := archiver.NewZip().Unarchive(pathToGameZip, pathToGame); err != nil {
		return err
	}

	fmt.Printf("Game %s is ready.\n", game.Name)

	return err
}

func GamesList() {
	ref, err := cliref.Get()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get CliRef: %s\n", err)
		os.Exit(cli.CliRefFailed)
	}

	fmt.Println("Supported games:")

	// Display all games
	for _, game := range ref.Games {
		fmt.Printf("- %s\n", game.Name)
	}
}

func GameDelete(gameName string) {
	fmt.Printf("Deleting game: %s\n", gameName)

	pathToGame := filepath.Join(config.PathToGames, gameName)

	// Remove old game directory if it exists
	if err := os.RemoveAll(pathToGame); err != nil {
		fmt.Fprintf(os.Stderr, "deleting game failed: %s\n", err)
		os.Exit(cli.CliRefFailed)
	}

	fmt.Printf("Game %s successfully deleted.\n", gameName)
}

func GameUpdate(gameName string) {
	ref, err := cliref.Get()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get CliRef: %s\n", err)
		os.Exit(cli.CliRefFailed)
	}

	game, err := findGame(gameName, ref.Games)
	if err != nil {
		fmt.Fprintf(os.Stderr, err.Error())
		os.Exit(cli.Default)
	}

	versionCurrentStr, err := getCurrentGameVersion(gameName)
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get version for game %s with error: %s\n", gameName, err)
		os.Exit(cli.GameConfigProblem)
	}

	versionCurrent, err := version.NewVersion(versionCurrentStr)
	versionLatest, err := version.NewVersion(game.Version)

	if versionCurrent.LessThan(versionLatest) {
		fmt.Printf("Upgrading game %s from %s to %s...\n", game.Name, versionCurrent, versionLatest)
		if err := download(*game); err != nil {
			fmt.Fprintf(os.Stderr, "game download failed: %s\n", err)
			os.Exit(cli.GameDownloadFailed)
		}
	} else {
		fmt.Printf("Game %s is already up to date. Skiping update process...\n", game.Name)
	}
}

func getCurrentGameVersion(gameName string) (string, error) {
	type GameConfig struct {
		General struct {
			GameVersion string `json:"gameVersion"`
		} `json:"general"`
	}

	// Open game-config.json file
	pathToGameConfig := filepath.Join(config.PathToGames, gameName, "assets", "game-config.json")
	gameConfigByte, err := ioutil.ReadFile(pathToGameConfig)
	if err != nil {
		return "", err
	}

	// Find current game version
	gameConfig := GameConfig{}
	if err := json.Unmarshal(gameConfigByte, &gameConfig); err != nil {
		return "", err
	}

	return gameConfig.General.GameVersion, nil
}
