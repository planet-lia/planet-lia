package internal

import (
	"fmt"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"os"
	"time"
)

func Play(botPaths []string, matchFlags *MatchFlags, noMatchViewer bool, skipBotBuilds bool, serverPort int, numberOfMatches int) {
	if !skipBotBuilds {
		for _, botPath := range botPaths {
			BuildBot(botPath)
			fmt.Println(config.LineSeparator)
		}
	}

	// Run one match
	if numberOfMatches == 1 {
		_, err := GenerateMatch(botPaths, matchFlags)
		if err != nil {
			fmt.Fprintf(os.Stderr, "failed to generate match %s\n", err)
			os.Exit(1)
		}

		if !noMatchViewer {
			fmt.Println(config.LineSeparator)
			fmt.Println("Displaying the replay...")
			OpenMatchViewer(matchFlags.ReplayPath, serverPort)
		}
	}

	nSuccessfulMatches := 0
	nBot1Wins := 0
	nBot2Wins := 0
	start := time.Now()

	// Running multiple matches
	for i := 0; i < numberOfMatches; i++ {
		matchFlags.ReplayPath = "" // New replay path will be generated

		fmt.Printf("\n----------- Generating match %d/%d -----------\n", i+1, numberOfMatches)

		winnerIndex, err := GenerateMatch(botPaths, matchFlags)
		if err != nil {
			fmt.Fprintf(os.Stderr, "failed to generate match %d\nError: %s\n", i+1, err)
		} else {
			nSuccessfulMatches++
			if winnerIndex == 0 {
				nBot1Wins++
			} else if winnerIndex == 1 {
				nBot2Wins++
			}
			fmt.Println(config.LineSeparator)
			fmt.Printf("Winner: '%s' (index: %d)\n", botPaths[winnerIndex], winnerIndex)
			printTotalWins(botPaths[0], botPaths[1], nBot1Wins, nBot2Wins)
		}
	}

	elapsed := time.Since(start).Truncate(time.Millisecond)
	averageMatchDuration := elapsed / time.Duration(numberOfMatches)

	fmt.Println(config.LineSeparator)
	fmt.Println()
	fmt.Printf("Successfully generated %d/%d matches\n", nSuccessfulMatches, numberOfMatches)
	fmt.Printf("Total generation time:   %s\n", elapsed)
	fmt.Printf("Average generation time: %s\n", averageMatchDuration)
	fmt.Println()

	if !noMatchViewer {
		fmt.Println(config.LineSeparator)
		fmt.Println("Displaying the replay...")
		OpenMatchViewer("", serverPort)
	}
}

func printTotalWins(bot1Path, bot2Path string, nBot1Wins, nBot2Wins int) {
	fmt.Printf("Total wins: \n"+
		"   '%s': %d\n"+
		"   '%s': %d\n", bot1Path, nBot1Wins, bot2Path, nBot2Wins)
}
