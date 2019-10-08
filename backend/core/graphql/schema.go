package graphql

import (
	"github.com/graphql-go/graphql"
	"github.com/sirupsen/logrus"
)

var queryType = graphql.NewObject(graphql.ObjectConfig{
	Name: "Query",
	Fields: graphql.Fields{
		"about": &aboutField,
	},
})

func GenerateSchema() graphql.Schema {
	schemaConfig := graphql.SchemaConfig{Query: queryType}

	schema, err := graphql.NewSchema(schemaConfig)
	if err != nil {
		logrus.WithField("error", err).Fatal("Failed to create GraphQL schema")
	}

	return schema
}

var Schema = GenerateSchema()
