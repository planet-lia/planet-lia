package cmd

import (
	"github.com/planet-lia/planet-lia/backend/core/k8s"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/planet-lia/planet-lia/backend/core/minio"
	"github.com/planet-lia/planet-lia/backend/core/onlineEditor"
	"github.com/planet-lia/planet-lia/backend/core/redis"
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
	"time"
)

var rootCmd = &cobra.Command{
	Use:   "planet-lia-backend",
	Short: "Backend for the Planet Lia Platform",
	Long:  `Backend for the Planet Lia Platform`,
	Run: func(cmd *cobra.Command, args []string) {
		checkVerbose()
		logrus.WithField("version", version.Ver.String()).Info("Running Planet Lia Backend")
		logrus.WithFields(GetConfigsForLogging()).Info("Config")

		serverShutdown := make(chan bool)
		editorShutdown := make(chan bool)
		onlineEditorManagerGCShutdown := make(chan bool)

		sig := make(chan os.Signal, 1)
		signal.Notify(sig, os.Interrupt)
		go func() {
			for range sig {
				logging.Info("Shutting down server", logging.EmptyFields)
				serverShutdown <- true
				editorShutdown <- true
				onlineEditorManagerGCShutdown <- true

				logging.Info("Closing Redis client connection", logging.EmptyFields)
				redis.ClientClose(redis.Client)
			}
		}()

		// Create Redis client
		redis.Client = redis.NewClient()

		// Create Minio client
		minio.Client = minio.NewClient()
		if err := minio.InitialBuckets(minio.Client); err != nil {
			logging.Fatal("Failed to create initial buckets", logrus.Fields{"error": err})
		}

		// Create Kubernetes client
		var err error
		if k8s.Client, err = k8s.NewClient(); err != nil {
			logging.Fatal("Failed to create Kubernetes client", logrus.Fields{"error": err})
		}

		// Online editor
		go onlineEditor.GarbageCollector(time.Second*60, editorShutdown)
		onlineEditor.Checks(k8s.Client)
		go onlineEditor.ManagerStart(k8s.Client)
		if !viper.GetBool("online-editor-disable-manager-gc") {
			go onlineEditor.ManagerGarbageCollectorStart(k8s.Client, onlineEditorManagerGCShutdown)
		} else {
			logging.Info("Running without online editor manager garbage collector", logging.EmptyFields)
		}

		// Start HTTP server
		server.Start(serverShutdown)
	},
}

func init() {
	rootCmd.Flags().Bool("verbose", false, "Verbose logging")
	rootCmd.Flags().Bool("log-plain", false, "Log output will be formatted in plain instead of JSON")

	const defaultPort = 8080
	rootCmd.Flags().IntP("port", "p", defaultPort, "HTTP server port")
	rootCmd.Flags().IPP("http-bind", "b", net.IPv4(0, 0, 0, 0), "Bind HTTP server to IP")
	rootCmd.Flags().String("url", "http://127.0.0.1:"+strconv.Itoa(defaultPort), "")

	rootCmd.Flags().Bool("graphiql", true, "Enable GraphiQL (GraphQL web IDE)")

	rootCmd.Flags().String("redis-host", "localhost", "Redis Server hostname")
	rootCmd.Flags().Int("redis-port", 6379, "Redis server port")
	rootCmd.Flags().Int("redis-db", 0, "Redis server database")
	rootCmd.Flags().String("redis-password", "", "Redis server password")

	rootCmd.Flags().String("jwt-internal", "foo", "Internal JWT secret token")

	rootCmd.Flags().String("minio-host", "localhost", "Minio host")
	rootCmd.Flags().Int("minio-port", 9000, "Minio port")
	rootCmd.Flags().String("minio-access-key", "admin", "Minio access key")
	rootCmd.Flags().String("minio-secret-key", "password", "Minio secret key")
	rootCmd.Flags().Bool("minio-disable-ssl", false, "Disable SSL when connecting to Minio")

	rootCmd.Flags().String("k8s-kubeconfig", "", "Use external Kubernetes cluster, provide kubeconfig file")

	rootCmd.Flags().String("online-editor-k8s-namespace", "online-editor-matches",
		"Namespace where to spawn online editor matches. Namespace must exists beforehand.")
	rootCmd.Flags().Bool("online-editor-k8s-namespace-create", false, "Creates online editor k8s namespace if it doesn't exist")
	rootCmd.Flags().Int("online-editor-max-cpu", 3, "Maximum number of cpu's for online editor's manager to allocate")
	rootCmd.Flags().Duration("online-editor-match-max-duration", time.Minute, "Maximum duration an online editor match can be executing (rounds down to nearest second)")
	rootCmd.Flags().String("online-editor-image", "planetlia/online-editor:0.0.5", "Online editor Docker image")
	rootCmd.Flags().Bool("online-editor-disable-manager-gc", false, "Disable the online editor manager garbage collector")
	rootCmd.Flags().Bool("online-editor-ignore-resource-requests", false, "Does not place K8s pod resource requests on the online editors container")
	rootCmd.Flags().Bool("online-editor-ignore-resource-limits", false, "Does not place K8s pod resource limits on the online editors container")

	envPrefix := "LIA"
	viper.SetEnvPrefix(envPrefix)

	// Flags can be accepted as ENV variables as well. The name of the ENV variable follows the following convention:
	// LIA_<FLAG ALL UPPER CASE WITH - replaced with _>
	// (e.g. LIA_HTTP_BIND)
	rootCmd.Flags().VisitAll(func(flag *pflag.Flag) {
		viper.BindEnv(flag.Name, envPrefix+"_"+strings.ToUpper(dashToUnderscore(flag.Name)))
		viper.BindPFlag(flag.Name, flag)
	})

	if !viper.GetBool("log-plain") {
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

var secretConfigKeys = []string{"jwt-internal", "redis-password"}

// Returns what viper.AllSettings() returns with the exception that keys which are in the slice `secretConfigKeys`
// their value will be set to "<redacted>". This allows us to log the returned value without worrying about exposing
// passwords in logs.
func GetConfigsForLogging() map[string]interface{} {
	c := viper.AllSettings()

	for _, key := range secretConfigKeys {
		if _, ok := c[key]; ok {
			c[key] = "<redacted>"
		}
	}

	return c
}
