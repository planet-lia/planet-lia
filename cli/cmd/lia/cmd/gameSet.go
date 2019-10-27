package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/spf13/cobra"
)

var gameSetCmd = &cobra.Command{
	Use:   "set <game-name>",
	Short: "Set a game to use",
	Long:  `"Set which game should commands use by default`,
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		internal.GameSet(args[0])
	},
}

func init() {
	gameCmd.AddCommand(gameSetCmd)
}
