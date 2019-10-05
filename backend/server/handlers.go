package server

import (
	"encoding/json"
	"github.com/planet-lia/planet-lia/backend/graphql"
	"github.com/planet-lia/planet-lia/backend/logging"
	"github.com/sirupsen/logrus"
	"net/http"
	"time"
)

func sendResponse(w *http.ResponseWriter, resp interface{}, status int) {
	(*w).Header().Set("Content-Type", "application/json")
	(*w).WriteHeader(status)
	respB, _ := json.MarshalIndent(resp, "", "  ")
	(*w).Write(respB)
}

func sendFailureResponse(w *http.ResponseWriter, error string, status int) {
	if error == "" && status == http.StatusInternalServerError {
		error = "Internal Server Error"
	}

	resp := struct {
		Success bool   `json:"success"`
		Error   string `json:"error"`
	}{
		false,
		error,
	}

	sendResponse(w, resp, status)
}

func rootHandler(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	query := `
{
  about {
    buildDate
    datetime
    name
    repo
    url
    version {
      commit
      full
      major
      minor
    }
  }
}
	`

	data, err := graphql.Query(ctx, query)
	if err != nil {
		logging.Error(ctx, "Failed to query GraphQL endpoint", logrus.Fields{"error": err})
		sendFailureResponse(&w, "internal server error", http.StatusInternalServerError)
		return
	}

	var response map[string]interface{}

	rJSON, err := json.Marshal(data["about"])

	err = json.Unmarshal(rJSON, &response)
	if err != nil {
		logging.Fatal(ctx, "Failed to marshal final value merge", logging.EmptyFields)
		sendFailureResponse(&w, "internal server error", http.StatusInternalServerError)
		return
	}

	response["success"] = true

	sendResponse(&w, response, http.StatusOK)
}

func healthHandler(w http.ResponseWriter, r *http.Request) {
	resp := struct {
		Success  bool   `json:"success"`
		Health   string `json:"health"`
		Datetime string `json:"datetime"`
	}{
		true,
		"healthy",
		time.Now().Format(time.RFC3339),
	}

	sendResponse(&w, &resp, http.StatusOK)
}

func notFoundHandler(w http.ResponseWriter, r *http.Request) {
	const status = http.StatusNotFound

	resp := struct {
		Success  bool   `json:"success"`
		Error    string `json:"error"`
		Datetime string `json:"datetime"`
	}{
		false,
		"404 - Not Found",
		time.Now().Format(time.RFC3339),
	}

	sendResponse(&w, &resp, status)
}
