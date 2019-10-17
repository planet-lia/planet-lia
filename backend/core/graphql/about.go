package graphql

import (
	"github.com/graphql-go/graphql"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/planet-lia/planet-lia/backend/core/version"
	"github.com/spf13/viper"
	"time"
)

var aboutObject = graphql.NewObject(
	graphql.ObjectConfig{
		Name:        "About",
		Description: "Basic information about the backend system.",
		Fields: graphql.Fields{
			"name": &graphql.Field{
				Description: "Name of service.",
				Type:        graphql.String,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					return "planet-lia-backend", nil
				},
			},
			"url": &graphql.Field{
				Description: "URL of the service",
				Type:        graphql.String,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					return viper.GetString("url"), nil
				},
			},
			"version": &graphql.Field{
				Description: "Version of the service.",
				Type: graphql.NewObject(graphql.ObjectConfig{
					Name: "Version",
					Fields: graphql.Fields{
						"major": &graphql.Field{
							Type: graphql.String,
							Resolve: func(_ graphql.ResolveParams) (interface{}, error) {
								return version.Ver.Major, nil
							},
						},
						"minor": &graphql.Field{
							Type: graphql.String,
							Resolve: func(_ graphql.ResolveParams) (interface{}, error) {
								return version.Ver.Minor, nil
							},
						},
						"commit": &graphql.Field{
							Description: "Commit ID",
							Type:        graphql.String,
							Resolve: func(_ graphql.ResolveParams) (interface{}, error) {
								return version.Ver.Commit, nil
							},
						},
						"full": &graphql.Field{
							Description: "Full version string",
							Type:        graphql.String,
							Resolve: func(_ graphql.ResolveParams) (interface{}, error) {
								return version.Ver.Full(), nil
							},
						},
					},
				}),
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					return p, nil
				},
			},
			"buildDate": &graphql.Field{
				Description: "Date when the service was built.",
				Type:        graphql.String,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					return version.Ver.BuildDate, nil
				},
			},
			"repo": &graphql.Field{
				Description: "URL of repository.",
				Type:        graphql.String,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					return "https://github.com/planet-lia/planet-lia", nil
				},
			},
			"datetime": &graphql.Field{
				Description: "Current datetime of service (useful to see if cache is in the way).",
				Type:        graphql.DateTime,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					return time.Now().UTC(), nil
				},
			},
		},
	},
)

var aboutField = graphql.Field{
	Name:        "About",
	Description: "Basic information about service.",
	Type:        aboutObject,
	Resolve: func(p graphql.ResolveParams) (interface{}, error) {
		logging.DebugC(p.Context, "Creating GraphQL about object", logging.EmptyFields)
		return p, nil
	},
}
