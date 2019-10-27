package internal

import (
	"encoding/json"
	"fmt"
	"github.com/hashicorp/go-version"
	"github.com/mholt/archiver"
	"github.com/planet-lia/planet-lia/cli"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"github.com/planet-lia/planet-lia/cli/internal/releases"
	"github.com/planet-lia/planet-lia/cli/pkg/utils"
	"github.com/spf13/viper"
	"io/ioutil"
	"os"
	"path/filepath"
)

func GameDownload(gameName string) {
	game, err := GetGameData(gameName)
	if err != nil {
		fmt.Fprintf(os.Stderr, err.Error())
		os.Exit(cli.Default)
	}

	if err := download(*game); err != nil {
		fmt.Fprintf(os.Stderr, "game download failed: %s\n", err)
		os.Exit(cli.GameDownloadFailed)
	}
}

func GetGameData(gameName string) (*releases.GameData, error) {
	rel, err := releases.Get()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get Releases: %s\n", err)
		os.Exit(cli.CliRefFailed)
	}

	for _, game := range rel.Games {
		if game.Name == gameName {
			return &game, nil
		}
	}
	return nil, fmt.Errorf("game '%s' not found, run `./lia game list` to see supported games", gameName)
}

func download(game releases.GameData) error {
	fmt.Printf("Downloading game: %s\n", game.Name)

	// Create games directory where executable is located
	if err := os.MkdirAll(config.PathToGames, os.ModePerm); err != nil {
		return err
	}

	pathToGameZip := filepath.Join(config.PathToGames, game.Name+".zip")
	pathToGame := filepath.Join(config.PathToGames, game.Name)

	// Create the zip file
	gameZipFile, err := os.Create(pathToGameZip)
	if err != nil {
		return err
	}
	defer func() {
		gameZipFile.Close()
		if err := os.Remove(pathToGameZip); err != nil {
			fmt.Fprintf(os.Stderr, "failed to remove game zip %s\n", pathToGameZip)
		}
	}()

	// Download file
	if err := utils.DownloadFile(game.DownloadURL, gameZipFile, 60); err != nil {
		return err
	}

	// Remove old game directory if it exists
	if err := os.RemoveAll(pathToGame); err != nil {
		return err
	}

	// Extract the zip
	if err := archiver.NewZip().Unarchive(pathToGameZip, pathToGame); err != nil {
		return err
	}

	fmt.Printf("Game '%s' is ready.\n", game.Name)

	return err
}

func GamesList() {
	rel, err := releases.Get()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get Releases: %s\n", err)
		os.Exit(cli.CliRefFailed)
	}

	fmt.Println("Supported games:")

	// Display all games
	for _, game := range rel.Games {
		fmt.Printf("- %s\n", game.Name)
	}
}

func GameSet(gameName string) {
	pathToGame := filepath.Join(config.PathToGames, gameName)

	// Abort if game does not exist
	if exists, err := utils.DirectoryExists(pathToGame); err != nil || !exists {
		fmt.Fprintf(os.Stderr, "failed to set game '%s', directory '%s' does not exist\n%s\n",
			gameName, pathToGame, err)
		os.Exit(cli.Default)
	}

	// Set selected game
	viper.Set("selectedGame", gameName)
	if err := viper.WriteConfig(); err != nil {
		fmt.Fprintf(os.Stderr, "failed to set selected game in config file: %s\n", err)
	} else {
		fmt.Printf("Game %s is now selected.\n", gameName)
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
	game, err := GetGameData(gameName)
	if err != nil {
		fmt.Fprintf(os.Stderr, err.Error())
		os.Exit(cli.Default)
	}

	versionCurrentStr, err := getCurrentGameVersion(gameName)
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get version for game '%s' with error: %s\n", gameName, err)
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
