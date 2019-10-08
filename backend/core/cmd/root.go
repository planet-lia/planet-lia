package cmd

import (
	"github.com/planet-lia/planet-lia/backend/core/server"
	"github.com/planet-lia/planet-lia/backend/core/version"
	"github.com/sirupsen/logrus"
	"github.com/spf13/cobra"
	"github.com/spf13/pflag"
	"github.com/spf13/viper"
	"net"
	"os"
	"os/signal"
	"strconv"
	"strings"
)

var (
	port    int
	bindIP  net.IP
	verbose bool
	logJson bool
)

var rootCmd = &cobra.Command{
	Use:   "planet-lia-backend",
	Short: "Backend for the Planet Lia Platform",
	Long:  `Backend for the Planet Lia Platform`,
	Run: func(cmd *cobra.Command, args []string) {
		checkVerbose()
		logrus.WithField("version", version.Ver.String()).Info("Running Planet Lia Backend")
		logrus.WithFields(viper.AllSettings()).Info("Config")

		serverShutdown := make(chan bool)

		sig := make(chan os.Signal, 1)
		signal.Notify(sig, os.Interrupt)
		go func() {
			for range sig {
				logrus.Info("Shutting down server")
				serverShutdown <- true
			}
		}()

		// Start HTTP server
		server.Start(serverShutdown)
	},
}

func init() {
	rootCmd.Flags().BoolVar(&verbose, "verbose", false, "verbose logging")
	rootCmd.Flags().BoolVar(&logJson, "log-json", true, "log output will be formatted in JSON")

	const defaultPort = 8080
	rootCmd.Flags().IntVarP(&port, "port", "p", defaultPort, "HTTP Server Port")
	rootCmd.Flags().IPVarP(&bindIP, "http-bind", "b", net.IPv4(0, 0, 0, 0), "bind HTTP server to IP")
	rootCmd.Flags().String("url", "http://127.0.0.1:"+strconv.Itoa(defaultPort), "")

	rootCmd.Flags().Bool("graphiql", true, "Enable GraphiQL (GraphQL web IDE)")

	envPrefix := "LIA"
	viper.SetEnvPrefix(envPrefix)

	// Flags can be accepted as ENV variables as well. The name of the ENV variable follows the following convention:
	// LIA_<FLAG ALL UPPER CASE WITH - replaced with _>
	// (e.g. LIA_HTTP_BIND)
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

func Execute() {
	rootCmd.AddCommand(versionCmd)

	if err := rootCmd.Execute(); err != nil {
		logrus.Fatal(err)
	}
}

func checkVerbose() {
	if viper.GetBool("verbose") {
		logrus.SetLevel(logrus.DebugLevel)
	}
}
