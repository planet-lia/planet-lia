package config

import (
	"fmt"
	"github.com/planet-lia/planet-lia/cli"
	"os"
	"path/filepath"
	"runtime"
)

const defaultReleasesPath = "http://localhost:8001/planet-lia-releases.json"
const SettingsFileExtension = "json"
const SettingsFile = ".planet-lia"

const PathToBotScripts = ".scripts"
const BuildScriptWindowsName = "build.bat"
const BuildScriptUnixName = "build.sh"
const CleanScriptWindowsName = "clean.bat"
const CleanScriptUnixName = "clean.sh"
const RunScriptWindowsName = "run.bat"
const RunScriptUnixName = "run.sh"

var OperatingSystem = runtime.GOOS

var ExecutableDirPath string
var PathToGames string
var ReleasesUrl string
var CurrentWorkingDirectory string

func init() {
	// Setup executable path
	ex, err := os.Executable()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get executable location\n%s\n", err)
		os.Exit(cli.OsCallFailed)
	}
	ExecutableDirPath = filepath.Dir(ex)

	PathToGames = filepath.Join(ExecutableDirPath, "games")

	// Planet Lia releases URL
	ReleasesUrl = os.Getenv("RELEASES_URL")
	if ReleasesUrl == "" {
		ReleasesUrl = defaultReleasesPath
	} else {
		fmt.Printf("ReleasesURL set to %s\n", ReleasesUrl)
	}

	// Current working directory
	cwd, err := os.Getwd()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get current working directory\n%s\n", err)
		os.Exit(cli.OsCallFailed)
	}
	CurrentWorkingDirectory = cwd
}
