package settings

import (
	"encoding/json"
	"fmt"
	"github.com/mitchellh/go-homedir"
	"github.com/planet-lia/planet-lia/cli"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"github.com/spf13/viper"
	"os"
	"path/filepath"
)

type DefaultSettings struct {
	SelectedGame string `json:"selectedGame"`
}

var Lia DefaultSettings

func Load() {
	err := viper.Unmarshal(&Lia)
	if err != nil {
		fmt.Fprintf(os.Stderr, "unable to decode Planet Lia config to struct: %s\n", err)
		os.Exit(cli.Default)
	}
}

// Create a new settings file in the user's default home directory using
// the default settings file contents.
func Create() error {
	home, err := homedir.Dir()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Failed to find homedir, could not generate .lia.json file")
		return err
	}

	newConfigPath := filepath.Join(home, config.SettingsFile+"."+config.SettingsFileExtension)

	f, err := os.Create(newConfigPath)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Failed to create config file in: %s\n", newConfigPath)
		return err
	}
	defer f.Close()

	defaultFileContents, _ := json.Marshal(DefaultSettings{""})
	_, err = f.Write(defaultFileContents)
	return err
}

func ExitIfNoGameSelected(commandName string, SelectedGame string) {
	printHelp := func() {
		fmt.Printf("Command `%s` cannot work without a game being selected.\n", commandName)
		fmt.Println("To select a game use `game set` command.")
		fmt.Println("If you don't have a game installed use `game download` command.")
		fmt.Println("List all supported games with `game list` command.")
	}

	if SelectedGame == "" {
		printHelp()
		os.Exit(cli.Default)
	}

	pathToGame := filepath.Join(config.PathToGames, SelectedGame)

	// Exit if game does not exist
	if _, err := os.Stat(pathToGame); os.IsNotExist(err) {
		fmt.Fprintf(os.Stderr, "game %s does not exist, directory %s does not exist\n error: %s\n",
			SelectedGame, pathToGame, err)
		printHelp()
		os.Exit(cli.Default)
	}
}
