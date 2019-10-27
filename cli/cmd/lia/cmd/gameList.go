package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/spf13/cobra"
)

var gameListCmd = &cobra.Command{
	Use:   "list",
	Short: "List supported games",
	Long:  `"List all supported Planet Lia games`,
	Args:  cobra.ExactArgs(0),
	Run: func(cmd *cobra.Command, args []string) {
		internal.GamesList()
	},
}

func init() {
	gameCmd.AddCommand(gameListCmd)
}
