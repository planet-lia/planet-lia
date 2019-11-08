package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/spf13/cobra"
)

var gameDeleteCmd = &cobra.Command{
	Use:   "delete <game-name>",
	Short: "Delete a game",
	Long:  `Delete a specific game`,
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		internal.GameDelete(args[0])
	},
}

func init() {
	gameCmd.AddCommand(gameDeleteCmd)
}
