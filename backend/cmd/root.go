package cmd

import (
	"github.com/planet-lia/planet-lia/backend/version"
	"github.com/sirupsen/logrus"
	"github.com/spf13/cobra"
	"github.com/spf13/pflag"
	"github.com/spf13/viper"
	"net"
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
		logrus.Info("Running Planet Lia Backend v:", version.Ver.String())
		logrus.WithFields(viper.AllSettings()).Info("Config")

		// TODO - Start HTTP Server
	},
}

func init() {
	rootCmd.Flags().IntVarP(&port, "port", "p", 8080, "HTTP Server Port")
	rootCmd.Flags().IPVarP(&bindIP, "http-bind", "b", net.IPv4(0, 0, 0, 0), "bind HTTP server to IP")
	rootCmd.Flags().BoolVar(&verbose, "verbose", false, "verbose logging")
	rootCmd.Flags().BoolVar(&verbose, "log-json", true, "log output will be formatted in JSON")

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
