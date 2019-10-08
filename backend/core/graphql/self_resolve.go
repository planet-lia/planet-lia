package graphql

import (
	"context"
	"encoding/json"
	"github.com/graphql-go/graphql"
	"github.com/pkg/errors"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/sirupsen/logrus"
)

var queryErrFailedToExec = errors.New("failed to execute graphql operation")

// Query the schema like an external request.
func Query(ctx context.Context, query string) (map[string]interface{}, error) {
	params := graphql.Params{Schema: Schema, RequestString: query, Context: ctx}
	res := graphql.Do(params)
	if len(res.Errors) > 0 {
		logging.Error(ctx, "Failed to marshal GraphQL response as JSON", logrus.Fields{"error": res.Errors})
		return nil, queryErrFailedToExec
	}

	rJSON, err := json.Marshal(res.Data)
	if err != nil {
		return nil, errors.Wrap(err, "Failed to marshal GraphQL response as JSON")
	}

	var data map[string]interface{}

	err = json.Unmarshal(rJSON, &data)
	if err != nil {
		return nil, errors.Wrap(err, "Failed to unmarshal GraphQL response as JSON")
	}

	return data, nil
}
