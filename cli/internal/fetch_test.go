package internal_test

import (
	"github.com/planet-lia/planet-lia/cli"
	"github.com/planet-lia/planet-lia/cli/internal"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"github.com/planet-lia/planet-lia/cli/tests"
	"io/ioutil"
	"os"
	"strconv"
	"testing"
)

func TestCmdFetch(t *testing.T) {
	cases := []struct {
		url             string
		name            string
		hasCustomBotDir bool
		exitStatus      int
		desc            string
	}{
		{
			url:             "https://github.com/liagame/java-bot/archive/master.zip",
			name:            "birko",
			hasCustomBotDir: false,
			exitStatus:      lia_SDK.OK,
			desc:            "downloading bot birko and put it into working dir",
		},
		{
			url:             "https://github.com/liagame",
			name:            "birko",
			hasCustomBotDir: false,
			exitStatus:      lia_SDK.BotDownloadFailed,
			desc:            "try to download non zip file",
		},
		{
			url:             "https://github.com/liagame.zip",
			name:            "birko",
			hasCustomBotDir: false,
			exitStatus:      lia_SDK.BotDownloadFailed,
			desc:            "try to download bot from non existent file",
		},
		{
			url:             "https://github.com/liagame/java-bot/archive/master.zip",
			name:            "birko",
			hasCustomBotDir: true,
			exitStatus:      lia_SDK.OK,
			desc:            "download bot mirko and put it into custom bot Dir",
		},
	}

	// Run actual tests
	for i, c := range cases {
		if os.Getenv("RUN_FUNC") == strconv.Itoa(i) {
			tests.SetupTmpConfigPaths()
			defer tests.CleanupTmpFiles()
			config.Setup()

			customBotDir := ""

			// Set custom bot dir
			if c.hasCustomBotDir {
				var err error
				customBotDir, err = ioutil.TempDir("", "")
				if err != nil {
					t.Fatal(err)
				}
			}
			defer func() {
				if err := os.RemoveAll(customBotDir); err != nil {
					t.Fatal(err)
				}
			}()

			// Run command
			internal.FetchBot(c.url, c.name, customBotDir)

			// Check custom bot dir
			if c.hasCustomBotDir {
				empty, err := tests.IsEmpty(customBotDir)
				if err != nil {
					t.Fatal(err)
				}
				if empty {
					t.Fatal("hasCustomBotDir should not be empty")
				}
			}

			return
		}
	}

	// Run test and check exit status
	for i, c := range cases {
		output, exitStatus := tests.GetCmdStatus("TestCmdFetch", i, false)
		if exitStatus != c.exitStatus {
			t.Logf("%s", c.desc)
			t.Logf("%s", output)
			t.Fatalf("exit status is %v but should be %v", exitStatus, c.exitStatus)
		}
	}
}
