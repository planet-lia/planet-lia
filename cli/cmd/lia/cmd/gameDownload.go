package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/spf13/cobra"
)

var gameDownloadCmd = &cobra.Command{
	Use:   "download <game-name>",
	Short: "Downloads a game",
	Long:  `Downloads a game based on provided name`,
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		internal.GameDownload(args[0])
	},
}

func init() {
	gameCmd.AddCommand(gameDownloadCmd)
}
