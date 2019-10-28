package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/planet-lia/planet-lia/cli/internal/settings"
	"github.com/spf13/cobra"
)

var viewReplay bool

var playCmd = &cobra.Command{
	Use:   "play <bot-1-path> <bot-2-path> ... <bot-n-path>",
	Short: "Builds bots and generate a match",
	Long:  `Builds provided bots and generates a match between them`,
	Args:  cobra.MinimumNArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		settings.ExitIfNoGameSelected("play", settings.Lia.SelectedGame)

		internal.Play(args, &matchFlags, viewReplay)
	},
}

func init() {
	rootCmd.AddCommand(playCmd)

	registerGenerateFlags(&matchFlags, playCmd)
	playCmd.Flags().BoolVarP(&viewReplay, "view", "v", true,
		"show replay when match is generated")
}
