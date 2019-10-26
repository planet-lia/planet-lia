package settings

import (
	"encoding/json"
	"fmt"
	"github.com/mitchellh/go-homedir"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"os"
	"path/filepath"
)

var defaultSettings = struct {
	SelectedGame string `json:"selectedGame"`
}{
	"",
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

	defaultFileContents, _ := json.Marshal(defaultSettings)
	_, err = f.Write(defaultFileContents)
	return err
}
