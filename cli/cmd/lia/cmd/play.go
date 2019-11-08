package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/planet-lia/planet-lia/cli/internal/settings"
	"github.com/spf13/cobra"
)

var noMatchViewer bool
var skipBotBuilds bool

var playCmd = &cobra.Command{
	Use:   "play <bot-1-path> <bot-2-path> ... <bot-n-path>",
	Short: "Builds bots and generate a match",
	Long:  `Builds provided bots and generates a match between them`,
	Args:  cobra.MinimumNArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		settings.ExitIfNoGameSelected("play", settings.Lia.SelectedGame)

		internal.Play(args, &matchFlags, noMatchViewer, skipBotBuilds, serverPort)
	},
}

func init() {
	rootCmd.AddCommand(playCmd)

	registerReplayFlags(&serverPort, playCmd)
	registerGenerateFlags(&matchFlags, playCmd)
	playCmd.Flags().BoolVarP(&noMatchViewer, "no-viewer", "n", false,
		"do not open match viewer to view the replay once the match is generated")
	playCmd.Flags().BoolVar(&skipBotBuilds, "skip-build", false,
		"skip building bots before generating the match")
}
