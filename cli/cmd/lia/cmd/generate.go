package cmd

import (
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/planet-lia/planet-lia/cli/internal/settings"
	"github.com/spf13/cobra"
)

var matchFlags = internal.MatchFlags{}

var generateCmd = &cobra.Command{
	Use:   "generate <bot-1-path> <bot-2-path> ... <bot-n-path>",
	Short: "Generates a match",
	Long:  `Generates a match without first building the bots`,
	Args:  cobra.MinimumNArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		settings.ExitIfNoGameSelected("generate", settings.Lia.SelectedGame)

		internal.GenerateMatch(args, &matchFlags)
	},
}

func init() {
	rootCmd.AddCommand(generateCmd)

	registerGenerateFlags(&matchFlags, generateCmd)
}

func registerGenerateFlags(matchFlags *internal.MatchFlags, command *cobra.Command) {
	command.Flags().BoolVarP(&matchFlags.Debug, "debug", "d", false, "show debug window")
	command.Flags().IntVarP(&matchFlags.Port, "port", "p", 0,
		"Port on which match generator will run")
	command.Flags().StringVarP(&matchFlags.ReplayPath, "replay", "r", "",
		"Choose custom replay name and location")
	command.Flags().StringVarP(&matchFlags.ConfigPath, "config", "c", "",
		"Specify custom config path")
	command.Flags().IntSliceVarP(&matchFlags.ManualBots, "manual-bots", "m", nil,
		"Set which bots need to be connected manually, examples: `-d 0,1` will debug bot1 and "+
			"bot2, `-d 1` will debug bot2, `-d` will only open a debug view but will connect all bots automatically)")
	command.Flags().Float32VarP(&matchFlags.WindowToScreenRatio, "window-to-screen", "w", 0,
		"Specify the ratio between debug view and the size of the monitor, it only works when also `-d/--debug` is provided")
	command.Flags().StringVarP(&matchFlags.BotListenerToken, "--bot-listener-token", "b", "",
		"Token with which an external service can connect and listen all communications between match-generator"+
			" and all bots. Disabled if not provided")
	command.Flags().StringVarP(&matchFlags.Teams, "--teams", "t", "",
		"Specify the teams for the bots in a format x:y:z:... which means that first x provided bots belongs "+
			"to the team 0, next y bots to team 1, next z to team 3 etc. Note that the teams format must be supported "+
			"by the game in order to work. If the parameter is not provided, the teams are set up automatically "+
			"depending on the game.")
}
