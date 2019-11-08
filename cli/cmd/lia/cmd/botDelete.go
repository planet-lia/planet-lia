package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/spf13/cobra"
)

var botDeleteCmd = &cobra.Command{
	Use:   "delete <bot-path>",
	Short: "Delete a bot",
	Long:  `"Delete a bot from the file system`,
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		internal.DeleteBot(args[0])
	},
}

func init() {
	botCmd.AddCommand(botDeleteCmd)
}
