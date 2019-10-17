package main

import (
	"errors"
	"fmt"
	"github.com/planet-lia/planet-lia/backend/online-editor/manager/game"
	"github.com/planet-lia/planet-lia/backend/online-editor/manager/version"
	"github.com/sirupsen/logrus"
	"github.com/spf13/cobra"
	"github.com/spf13/pflag"
	"github.com/spf13/viper"
	"os"
	"os/signal"
	"path/filepath"
	"strings"
)

func main() {
	if err := rootCmd.Execute(); err != nil {
		logrus.Fatal(err)
	}
}

var rootCmd = &cobra.Command{
	Use:   "online-editor-manager <matchId> <game> [<language> <botURL>]...",
	Short: "Online Editor manager",
	Args: func(cmd *cobra.Command, args []string) error {
		if len(args) < 4 {
			return errors.New("requires at least 4 arguments")
		}

		if len(args)%2 != 0 {
			return errors.New("bot language-url pairs are not even")
		}

		return nil
	},
	Run: func(cmd *cobra.Command, args []string) {
		checkVerbose()
		logrus.WithField("version", version.Ver.String()).Info("Running Online Editor Manager")
		logrus.WithFields(viper.AllSettings()).Info("Config")

		m := game.Match{
			MatchId:  args[0],
			GameName: args[1],
		}

		bots := make([]game.Bot, 0)
		botIndex := 1
		for i := 2; i < len(args); i += 2 {
			b := game.Bot{}
			b.Language = args[i]
			b.SourceUrl = args[i+1]
			b.Id = fmt.Sprintf("%s_bot%d", b.Language, botIndex)
			b.Path = filepath.Join(game.GameBotsDir(m.GameName), b.Id)

			bots = append(bots, b)
			botIndex++
		}

		logrus.WithField("noBots", len(bots)).Info("Parsed all bots")

		shutdown := make(chan bool)

		sig := make(chan os.Signal, 1)
		signal.Notify(sig, os.Interrupt)
		go func() {
			for range sig {
				shutdown <- true
			}
		}()

		game.Start(m, bots, shutdown)
	},
}

func init() {
	rootCmd.Flags().Bool("verbose", false, "Verbose logging")
	rootCmd.Flags().Bool("log-json", true, "Log output will be formatted in JSON")

	rootCmd.Flags().String("jwt", "foo", "JWT to access Backend Core")
	rootCmd.Flags().String("root-backend-endpoint", "http://localhost:8080", "Root Backend Core endpoint")
	rootCmd.Flags().String("lia-sdk", "/home/app/lia-sdk-linux/lia", "Filepath of lia-sdk executable")

	envPrefix := "LIA"
	viper.SetEnvPrefix(envPrefix)

	// Flags can be accepted as ENV variables as well. The name of the ENV variable follows the following convention:
	// LIA_<FLAG ALL UPPER CASE WITH - replaced with _>
	// (e.g. LIA_VERBOSE)
	rootCmd.Flags().VisitAll(func(flag *pflag.Flag) {
		viper.BindEnv(flag.Name, envPrefix+"_"+strings.ToUpper(dashToUnderscore(flag.Name)))
		viper.BindPFlag(flag.Name, flag)
	})

	if viper.GetBool("log-json") {
		logrus.SetFormatter(&logrus.JSONFormatter{})
	}
}

func dashToUnderscore(s string) string {
	return strings.Replace(s, "-", "_", -1)
}

func checkVerbose() {
	if viper.GetBool("verbose") {
		logrus.SetLevel(logrus.DebugLevel)
	}
}
