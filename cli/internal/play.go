package internal

import (
	"fmt"
	"github.com/planet-lia/planet-lia/cli/internal/config"
)

func Play(botPaths []string, gameFlags *MatchFlags, viewReplay bool) {
	for _, botPath := range botPaths {
		BuildBot(botPath)
		fmt.Println(config.LineSeparator)
	}

	GenerateMatch(botPaths, gameFlags)

	if viewReplay {
		fmt.Println("Displaying the replay...")
		//ShowReplayViewer(gameFlags.ReplayPath, replayViewerWidth)
	}
}
