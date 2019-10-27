package cmd

//
//import (
//	"github.com/planet-lia/planet-lia/cli/internal"
//	"github.com/planet-lia/planet-lia/cli/internal/analytics"
//	"github.com/spf13/cobra"
//)
//
//var gameFlags = internal.GameFlags{}
//
//var generateCmd = &cobra.Command{
//	Use:   "generate <bot1Dir> <bot2Dir>",
//	Short: "Generates a game",
//	Long:  `Generates a game. This is a low level command.`,
//	Args:  cobra.ExactArgs(2),
//	Run: func(cmd *cobra.Command, args []string) {
//		bot1Dir := args[0]
//		bot2Dir := args[1]
//
//		analytics.Log("command", "generate", map[string]string{
//			"gseed": analytics.ParseIntFlagToString(cmd, "gseed"),
//			"mseed": analytics.ParseIntFlagToString(cmd, "mseed"),
//			"port":  analytics.ParseIntFlagToString(cmd, "port"),
//			"map":   analytics.TrimPath(analytics.ParseStringFlag(cmd, "map")),
//			"debug": analytics.ParseIntSliceFlagToString(cmd, "debug"),
//		})
//
//		internal.GenerateGame(bot1Dir, bot2Dir, &gameFlags)
//	},
//}
//
//func init() {
//	rootCmd.AddCommand(generateCmd)
//
//	generateCmd.Flags().IntVarP(&gameFlags.GameSeed, "gseed", "g", 0, "game seed. 0 means random")
//	generateCmd.Flags().IntVarP(&gameFlags.MapSeed, "mseed", "m", 0, "map seed. 0 means random")
//	generateCmd.Flags().IntVarP(&gameFlags.Port, "port", "p", 0, "port on which game engine will run. Default is 8887")
//	generateCmd.Flags().StringVarP(&gameFlags.MapPath, "map", "M", "", "path to custom map settings")
//	generateCmd.Flags().StringVarP(&gameFlags.ReplayPath, "replay", "r", "", "choose custom replay name and location")
//	generateCmd.Flags().StringVarP(&gameFlags.ConfigPath, "config", "c", "", "choose custom config")
//	generateCmd.Flags().IntSliceVarP(&gameFlags.DebugBots, "debug", "d", []int{}, "specify which bots you want to run manually, "+
//		"examples: -d 1,2 -- debug bot1 and bot2, -d 2 -- debug bot2)")
//}
