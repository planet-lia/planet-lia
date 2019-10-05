package version

import (
	"fmt"
)

const (
	versionMajor = 0
	versionMinor = 1
)

var (
	// Filled out when running go build using the following flag
	// -ldflags "-X ${IMPORT_PATH}/version.buildDate=${BUILD_DATE} -X ${IMPORT_PATH}/version.commit=${COMMIT}"
	buildDate string
	commit    string

	Ver Version
)

type Version struct {
	Major     int    `json:"major"`
	Minor     int    `json:"minor"`
	Commit    string `json:"commit"`
	BuildDate string `json:"buildDate"`
}

func (v Version) String() string {
	return fmt.Sprintf("%d.%d-%s", v.Major, v.Minor, v.Commit)
}

func (v Version) Full() string {
	return fmt.Sprintf("%d.%d-%s (Build date: %s)", v.Major, v.Minor, v.Commit, v.BuildDate)
}

func init() {
	Ver = Version{
		versionMajor,
		versionMinor,
		commit,
		buildDate,
	}
}
