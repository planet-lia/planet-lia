package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/planet-lia/planet-lia/cli/internal/settings"
	"github.com/spf13/cobra"
)

var botNewCmd = &cobra.Command{
	Use:   "new <language> <bot-name>",
	Short: "Download a new bot",
	Long:  `"Downloads a bot in a current working directory and sets the name`,
	Args:  cobra.ExactArgs(2),
	Run: func(cmd *cobra.Command, args []string) {
		settings.ExitIfNoGameSelected("bot new", settings.Lia.SelectedGame)

		internal.FetchBot(settings.Lia.SelectedGame, args[0], args[1])
	},
}

func init() {
	botCmd.AddCommand(botNewCmd)
}
