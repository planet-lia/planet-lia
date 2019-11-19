package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/planet-lia/planet-lia/cli/internal/settings"
	"github.com/spf13/cobra"
)

var noMatchViewer bool
var skipBotBuilds bool
var numberOfMatches int

var playCmd = &cobra.Command{
	Use:   "play <bot-1-path> <bot-2-path> ... <bot-n-path>",
	Short: "Builds bots and generate a match",
	Long:  `Builds provided bots and generates a match between them`,
	Args:  cobra.MinimumNArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		settings.ExitIfNoGameSelected("play", settings.Lia.SelectedGame)

		internal.Play(args, &matchFlags, noMatchViewer, skipBotBuilds, serverPort, numberOfMatches)
	},
}

func init() {
	rootCmd.AddCommand(playCmd)

	registerReplayFlags(&serverPort, playCmd)
	registerGenerateFlags(&matchFlags, playCmd)
	playCmd.Flags().BoolVar(&noMatchViewer, "no-viewer", false,
		"Do not open match viewer to view the replay once the match is generated")
	playCmd.Flags().BoolVar(&skipBotBuilds, "skip-build", false,
		"Skip building bots before generating the match")
	playCmd.Flags().IntVarP(&numberOfMatches, "num-matches", "n", 1,
		"Choose how many matches to generate")
}
