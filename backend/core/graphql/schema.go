package graphql

import (
	"github.com/graphql-go/graphql"
	"github.com/sirupsen/logrus"
)

var queryType = graphql.NewObject(graphql.ObjectConfig{
	Name: "Query",
	Fields: graphql.Fields{
		"about":                  &aboutField,
		"onlineEditorMatchState": &onlineEditorMatchStateField,
	},
})

var mutationType = graphql.NewObject(graphql.ObjectConfig{
	Name: "Mutation",
	Fields: graphql.Fields{
		"onlineEditorSubmit": &onlineEditorSubmitMutationObject,
	},
})

func GenerateSchema() graphql.Schema {
	schemaConfig := graphql.SchemaConfig{Query: queryType, Mutation: mutationType}

	schema, err := graphql.NewSchema(schemaConfig)
	if err != nil {
		logrus.WithField("error", err).Fatal("Failed to create GraphQL schema")
	}

	return schema
}

var Schema = GenerateSchema()
