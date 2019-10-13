package graphql

import (
	"errors"
	"github.com/graphql-go/graphql"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/planet-lia/planet-lia/backend/core/onlineEditor"
	"github.com/sirupsen/logrus"
)


var onlineEditorSubmitInputBotObject = graphql.NewInputObject(graphql.InputObjectConfig{
	Name:        "OnlineEditorSubmitInputBot",
	Fields:      graphql.InputObjectConfigFieldMap{
		"language": &graphql.InputObjectFieldConfig{
			Type: graphql.NewNonNull(LanguageType),
		},
		"source": &graphql.InputObjectFieldConfig{
			Description: "Base64 encoded bot source code",
			Type: graphql.NewNonNull(graphql.String),
		},
	},
})

var onlineEditorSubmitMutationObject = graphql.Field{
	Description: "Submit source code to generate a match",
	Type: graphql.NewObject(graphql.ObjectConfig{
		Name: "OnlineEditorMatchId",
		Fields: graphql.Fields{
			"id": &graphql.Field{
				Type: graphql.ID,
				Resolve: func(p graphql.ResolveParams) (i interface{}, e error) {
					return p.Source, nil
				},
			},
		},
	}),
	Args: graphql.FieldConfigArgument{
		"game": &graphql.ArgumentConfig{
			Description: "Currently only 'lia-1'",
			Type:        graphql.NewNonNull(graphql.String),
		},
		"bots": &graphql.ArgumentConfig{
			Description: "Currently only 'lia-1'",
			Type:        graphql.NewList(onlineEditorSubmitInputBotObject),
		},
	},
	Resolve: func(p graphql.ResolveParams) (interface{}, error) {
		game := p.Args["game"].(string)
		bots := p.Args["bots"].([]interface{})

		if len(bots) < 2 {
			return nil, errors.New("at least two bots required")
		}

		bots_ := make([]onlineEditor.InputBot, 0, len(bots))

		for _, b := range bots {
			bot := b.(map[string]interface{})
			language := bot["language"].(string)
			source := bot["source"].(string)

			if !isValidBase64EncodedStringNonEmpty(source) {
				return nil, errors.New("invalid base64 encoded bot source")
			}

			bot_ := onlineEditor.InputBot{
				Language: language,
				Source:   source,
			}
			bots_ = append(bots_, bot_)
		}

		logging.InfoC(p.Context, "Submitting match for online editor", logrus.Fields{"game": game,
			"noBots": len(bots)})

		mid, err := onlineEditor.Submit(p.Context, game, bots_)
		if err != nil {
			return nil, err
		}

		return mid, nil
	},
}

var onlineEditorMatchStateObject = graphql.NewObject(
	graphql.ObjectConfig{
		Name: "OnlineEditorMatchState",
		Fields: graphql.Fields{
			"matchId": &graphql.Field{
				Type: graphql.ID,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					m := p.Source.(onlineEditor.Match)
					return m.Id, nil
				},
			},
			"log": &graphql.Field{
				Description: "Match logs",
				Type:        graphql.String,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					m := p.Source.(onlineEditor.Match)
					return m.Log, nil
				},
			},
			"replayUrl": &graphql.Field{
				Description: "URL of the replay file",
				Type:        graphql.String,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					// TODO
					//m := p.Source.(onlineEditor.Match)
					return nil, nil
				},
			},
			"game": &graphql.Field{
				Type:        graphql.String,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					m := p.Source.(onlineEditor.Match)
					return m.Game, nil
				},
			},
			"bots": &graphql.Field{
				Type:              graphql.NewList(graphql.NewObject(graphql.ObjectConfig{
					Name:        "Bot",
					Fields:      graphql.Fields{
						"name": &graphql.Field{
							Type:              graphql.String,
							Resolve: func(p graphql.ResolveParams) (i interface{}, e error) {
								b := p.Source.(onlineEditor.InputBot)
								return b.Name, nil
							},
						},
						"language": &graphql.Field{
							Type:              LanguageType,
							Resolve: func(p graphql.ResolveParams) (i interface{}, e error) {
								b := p.Source.(onlineEditor.InputBot)
								return b.Language, nil
							},
						},
					},
				})),
				Resolve: func(p graphql.ResolveParams) (i interface{}, e error) {
					m := p.Source.(onlineEditor.Match)
					return m.Bots, nil
				},
			},
			"queuePosition": &graphql.Field{
				Description: "Position in queue for match generation. -1 signifies an error.",
				Type:        graphql.Int,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					m := p.Source.(onlineEditor.Match)
					return m.QueuePosition, nil
				},
			},
			"status": &graphql.Field{
				Type:        OnlineEditorMatchStatus,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					m := p.Source.(onlineEditor.Match)
					return m.Status, nil
				},
			},
			"created": &graphql.Field{
				Description: "When the match was created",
				Type:        graphql.DateTime,
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					m := p.Source.(onlineEditor.Match)
					return m.Created.UTC(), nil
				},
			},
		},
	},
)

var onlineEditorMatchStateField = graphql.Field{
	Name: "OnlineEditorMatchState",
	Description: "State of an online editor match",
	Type: onlineEditorMatchStateObject,
	Args: map[string]*graphql.ArgumentConfig{
		"matchId": {
			Type: graphql.ID,
		},
	},
	Resolve: func(p graphql.ResolveParams) (interface{}, error) {
		matchIdStr := p.Args["matchId"].(string)
		mId := onlineEditor.MatchId(matchIdStr)

		logging.InfoC(p.Context, "Getting state of online editor match", logrus.Fields{"matchId": mId})

		m, err := onlineEditor.GetMatchState(p.Context, mId)
		if err != nil {
			return nil, err
		}

		err = onlineEditor.ExtendExpirationMatchState(p.Context, mId)
		if err != nil {
			logging.ErrorC(p.Context, "Failed to extend expiration of online editor match key",
				logrus.Fields{"error": err, "matchId": mId})
			return nil, errors.New("failed to extend expiration")
		}

		return m, nil
	},
}
