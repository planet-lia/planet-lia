package internal

import (
	"fmt"
)

const (
	versionMajor  = 0
	versionMinor  = 1
	versionBugfix = 0
)

var (
	Ver Version
)

type Version struct {
	Major  int `json:"major"`
	Minor  int `json:"minor"`
	Bugfix int `json:"bugfix"`
}

func (v Version) String() string {
	return fmt.Sprintf("%d.%d.%d", v.Major, v.Minor, v.Bugfix)
}

func init() {
	Ver = Version{
		versionMajor,
		versionMinor,
		versionBugfix,
	}
}
