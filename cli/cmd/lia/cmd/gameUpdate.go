package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/spf13/cobra"
)

var gameUpdateCmd = &cobra.Command{
	Use:   "update <game-name>",
	Short: "Update a game",
	Long:  `"Updates a Planet Lia game to a specific version`,
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		internal.GameUpdate(args[0])
	},
}

func init() {
	gameCmd.AddCommand(gameUpdateCmd)
}
