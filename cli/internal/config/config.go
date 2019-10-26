package config

import (
	"fmt"
	"github.com/planet-lia/planet-lia/cli"
	"os"
	"path/filepath"
	"runtime"
)

const defaultReleasesPath = "http://localhost:8000/planet-lia-releases.json"
const SettingsFileExtension = "json"
const SettingsFile = ".planet-lia"

var OperatingSystem = runtime.GOOS

var ExecutableDirPath string
var PathToGames string
var ReleasesUrl string

func init() {
	// Setup executable path
	ex, err := os.Executable()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get executable location\n %s", err)
		os.Exit(cli.FailedToGetEnvironment)
	}
	ExecutableDirPath = filepath.Dir(ex)

	PathToGames = filepath.Join(ExecutableDirPath, "games")

	// Planet Lia releases URL
	ReleasesUrl := os.Getenv("RELEASES_URL")
	if ReleasesUrl == "" {
		ReleasesUrl = defaultReleasesPath
	} else {
		fmt.Printf("ReleasesURL set to %s\n", ReleasesUrl)
	}
}
