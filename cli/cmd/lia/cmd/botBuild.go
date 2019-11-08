package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/spf13/cobra"
)

var botBuildCmd = &cobra.Command{
	Use:   "build <bot-path>",
	Short: "Prepare a bot so that it will be able to play matches",
	Long:  ``,
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		internal.BuildBot(args[0])
	},
}

func init() {
	botCmd.AddCommand(botBuildCmd)
}
