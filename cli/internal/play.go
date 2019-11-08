package internal

import (
	"fmt"
	"github.com/planet-lia/planet-lia/cli/internal/config"
)

func Play(botPaths []string, matchFlags *MatchFlags, noMatchViewer bool, skipBotBuilds bool, serverPort int) {
	if !skipBotBuilds {
		for _, botPath := range botPaths {
			BuildBot(botPath)
			fmt.Println(config.LineSeparator)
		}
	}

	GenerateMatch(botPaths, matchFlags)

	if !noMatchViewer {
		fmt.Println("Displaying the replay...")
		OpenMatchViewer(matchFlags.ReplayPath, serverPort)
	}
}
