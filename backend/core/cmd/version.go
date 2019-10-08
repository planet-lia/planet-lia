package cmd

import (
	"fmt"
	"github.com/planet-lia/planet-lia/backend/core/version"
	"github.com/spf13/cobra"
)

var versionCmd = &cobra.Command{
	Use:   "version",
	Short: "Print the version number of sensor-server",
	Long:  `All software has versions. This is sensor-server's'`,
	Run: func(cmd *cobra.Command, args []string) {
		v := version.Ver
		fmt.Printf("Version: %s\n", v.String())
		fmt.Printf("Build Date: %s\n", v.BuildDate)
	},
}
