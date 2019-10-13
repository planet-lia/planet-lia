package graphql

import (
	"github.com/graphql-go/graphql"
	"github.com/planet-lia/planet-lia/backend/core/onlineEditor"
)

var LanguageType = graphql.NewEnum(graphql.EnumConfig{
	Name:        "Language",
	Description: "Programming Language",
	Values:      graphql.EnumValueConfigMap{
		"PYTHON3": &graphql.EnumValueConfig{
			Value: "python3",
		},
		"JAVA": &graphql.EnumValueConfig{
			Value: "java",
		},
		"KOTLIN": &graphql.EnumValueConfig{
			Value: "kotlin",
		},
	},
})

var OnlineEditorMatchStatus = graphql.NewEnum(graphql.EnumConfig{
	Name:        "OnlineEditorMatchStatus",
	Values:      graphql.EnumValueConfigMap{
		"QUEUED": &graphql.EnumValueConfig{
			Value: onlineEditor.MatchStatusQueued,
		},
		"GENERATING": &graphql.EnumValueConfig{
			Value: onlineEditor.MatchStatusGenerating,
		},
		"SUCCESS": &graphql.EnumValueConfig{
			Value: onlineEditor.MatchStatusSuccess,
		},
		"FAILURE": &graphql.EnumValueConfig{
			Value: onlineEditor.MatchStatusFailure,
		},
	},
})
