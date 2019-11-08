package cmd

import (
	"fmt"
	"github.com/mitchellh/go-homedir"
	"github.com/planet-lia/planet-lia/cli"
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"github.com/planet-lia/planet-lia/cli/internal/settings"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
	"os"
)

var cfgFile string
var showVersion bool

var rootCmd = &cobra.Command{
	Use:   "lia",
	Short: "Planet Lia CLI tool",
	Long:  `The command line interface tool for Planet Lia games`,
	Run: func(cmd *cobra.Command, args []string) {

		if showVersion {
			fmt.Printf("Planet Lia CLI version: %s\n", internal.Ver.String())
		} else {
			_ = cmd.Help()
		}
	},
}

// Execute adds all child commands to the root command and sets flags appropriately.
// This is called by main.main(). It only needs to happen once to the rootCmd.
func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(cli.Default)
	}
}

func init() {
	cobra.OnInitialize(initConfig)
	cobra.MousetrapHelpText = `This is a command line tool.

You need to run it from PowerShell or Cmd.
`

	rootCmd.Flags().BoolVarP(&showVersion, "version", "v", false, "show version")
}

// initConfig reads in config file and ENV variables if set.
func initConfig() {

	viper.SetConfigType(config.SettingsFileExtension)

	if cfgFile != "" {
		// Use config file from the flag.
		viper.SetConfigFile(cfgFile)
	} else {
		// Find home directory.
		home, err := homedir.Dir()
		if err != nil {
			fmt.Println(err)
			os.Exit(cli.LiaSettingsFailure)
			return
		}

		// Search config in home directory for lia settings file
		viper.AddConfigPath(home)
		viper.SetConfigName(config.SettingsFile)
	}

	viper.AutomaticEnv() // read in environment variables that match

	// If a config file is found, read it in.
	if err := viper.ReadInConfig(); err == nil {
		fmt.Println("Using settings file:", viper.ConfigFileUsed())

	} else {
		// In case we don't find a planet lia settings file, create one
		// using the default parameters
		fmt.Println("Creating new settings file.")
		if err := settings.Create(); err != nil {
			fmt.Printf("Failed to create Planet Lia settings file. Error: %v\n", err)
			os.Exit(cli.LiaSettingsFailure)
			return
		}

		// Successfully created new settings file
		if err := viper.ReadInConfig(); err != nil {
			fmt.Printf("Failed to read from newly created settings file. Error: %v\n", err)
			os.Exit(cli.LiaSettingsFailure)
			return
		}
	}

	settings.Load()

	if settings.Lia.SelectedGame != "" {
		fmt.Printf("Selected game: %s (to change the game use `game set` command)\n", settings.Lia.SelectedGame)
	} else {
		fmt.Println("No game selected (to select the game use `game set` command)")
	}
	fmt.Println(config.LineSeparator)
}
