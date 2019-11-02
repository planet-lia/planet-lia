package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"github.com/spf13/cobra"
)

var serverPort int

var replayCmd = &cobra.Command{
	Use:   "replay [pathToReplay]",
	Short: "Opens a match viewer",
	Long: `Opens a match viewer. If path to the replay file is set as an
argument then that replay is played, else replay picker is opened.`,
	Args: cobra.MaximumNArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		replayFile := ""
		if len(args) == 1 {
			replayFile = args[0]
		}

		internal.OpenMatchViewer(replayFile, serverPort)
	},
}

func init() {
	rootCmd.AddCommand(replayCmd)
	registerReplayFlags(&serverPort, replayCmd)
}

func registerReplayFlags(serverPort *int, command *cobra.Command) {
	command.Flags().IntVarP(serverPort, "server-port", "s", config.DefaultServerPort,
		"select a port on which to serve match-viewer, replay files and assets")
}
