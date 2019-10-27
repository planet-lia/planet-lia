package cmd

import (
	"github.com/spf13/cobra"
)

var botCmd = &cobra.Command{
	Use:   "bot <language> <name>",
	Short: "Manage bots",
	Long:  `Manage bots for any Planet Lia game`,
	Args:  cobra.ExactArgs(0),
	Run: func(cmd *cobra.Command, args []string) {
		_ = cmd.Help()
	},
}

func init() {
	rootCmd.AddCommand(botCmd)
}
