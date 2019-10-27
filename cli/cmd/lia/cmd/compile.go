package cmd

//
//import (
//	"fmt"
//	"github.com/planet-lia/planet-lia/cli"
//	"github.com/planet-lia/planet-lia/cli/internal"
//	"github.com/planet-lia/planet-lia/cli/internal/analytics"
//	"github.com/spf13/cobra"
//	"os"
//)
//
//var compileCmd = &cobra.Command{
//	Use:   "compile <botDir>",
//	Short: "Compiles/prepares bot in specified dir",
//	Long:  `Compiles or prepares (depending on the language) the bot in specified dir.`,
//	Args:  cobra.ExactArgs(1),
//	Run: func(cmd *cobra.Command, args []string) {
//		botDir := args[0]
//
//		if err := internal.Compile(botDir); err != nil {
//			fmt.Printf("%s\n", err)
//			os.Exit(lia_SDK.PreparingBotFailed)
//		}
//	},
//}
//
//func init() {
//	rootCmd.AddCommand(compileCmd)
//}
