package config

import (
	"fmt"
	"github.com/planet-lia/planet-lia/cli"
	"os"
	"path/filepath"
	"runtime"
)

var OperatingSystem = runtime.GOOS

var ExecutableDirPath string
var PathToGames string

func init() {
	// Setup executable path
	ex, err := os.Executable()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to get executable location\n %s", err)
		os.Exit(cli.FailedToGetEnvironment)
	}
	ExecutableDirPath = filepath.Dir(ex)

	PathToGames = filepath.Join(ExecutableDirPath, "games")
}
