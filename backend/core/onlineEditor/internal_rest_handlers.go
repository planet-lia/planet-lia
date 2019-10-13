package onlineEditor

import (
	"encoding/json"
	"fmt"
	"github.com/dgrijalva/jwt-go"
	"github.com/gorilla/mux"
	"github.com/pkg/errors"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	"io/ioutil"
	"net/http"
	"strings"
)

func RegisterHandles(r *mux.Router) {
	r.HandleFunc("/match/{matchId}/bot/{bot}", matchBotHandler)
	r.HandleFunc("/match/{matchId}/state", matchStateHandler).Methods("POST")
}

func matchBotHandler(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	vars := mux.Vars(r)
	bot := vars["bot"]
	matchIdStr := vars["matchId"]

	if !handlerInternalJwtValidate(&w, r, matchIdStr) {
		// We have sent the failure response already
		return
	}

	mId := MatchId(matchIdStr) // We have validated this

	source, err := GetMatchBot(ctx, mId, bot)
	if err != nil {
		logging.WarningC(ctx, "Failed to get match bot", logrus.Fields{"error": err, "matchId": mId, "bot": bot})
		sendFailureResponse(&w, "Failed to get bot, does it exist?", http.StatusBadRequest)
		return
	}

	w.WriteHeader(http.StatusOK)
	w.Write([]byte(source))
}

func matchStateHandler(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	vars := mux.Vars(r)
	matchIdStr := vars["matchId"]

	if !handlerInternalJwtValidate(&w, r, matchIdStr) {
		// We have sent the failure response already
		return
	}

	mId := MatchId(matchIdStr) // We have validated this

	// Everything is authenticated and authorized...finally

	err := DisableExpirationMatchState(ctx, mId)
	if err != nil {
		logging.ErrorC(ctx, "Failed to disable expiration of online editor match key",
			logrus.Fields{"matchId": mId, "error": err})
	}

	type body struct {
		Status string `json:"status"`
		Log    string `json:"log"`
		Replay string `json:"replay"`
	}

	bodyData, err := ioutil.ReadAll(r.Body)
	if err != nil {
		logging.ErrorC(ctx, "Failed to read body of online editor match state request", logrus.Fields{"error": err})
		sendFailureResponse(&w, "500 - Internal Server Error", http.StatusInternalServerError)
		return
	}

	b := body{}

	err = json.Unmarshal(bodyData, &b)
	if err != nil {
		logging.WarningC(ctx, "Failed to unmarshal read body of online editor match state request", logrus.Fields{"error": err})
		sendFailureResponse(&w, "Failed to unmarshal request body", http.StatusBadRequest)
		return
	}

	if !(b.Status == MatchStatusGenerating || b.Status == MatchStatusSuccess || b.Status == MatchStatusFailure) {
		sendFailureResponse(&w, "invalid status field", http.StatusBadRequest)
		return
	}

	// Check to see if match exists
	exists, err := MatchExists(mId)
	if err != nil {
		logging.ErrorC(ctx, "Failed to check if match exists", logrus.Fields{"matchId": mId, "error": err})
		sendFailureResponse(&w, "500 - Internal Server Error", http.StatusInternalServerError)
		return
	}

	if !exists {
		// Match does not exist, it has expired. Signal the client this with HTTP status code 204
		logging.InfoC(ctx, "Online editor match has expired", logrus.Fields{"matchId": mId})
		sendFailureResponse(&w, "Match has expired", http.StatusNoContent)
		return
	}

	err = SetMatchData(ctx, mId, b.Status, b.Log, "")
	if err != nil {
		logging.ErrorC(ctx, "Failed to set online editor match data", logrus.Fields{"matchId": mId, "error": err})
		sendFailureResponse(&w, "500 - Internal Server Error", http.StatusInternalServerError)
		return
	}

	sendResponse(&w, struct {
		Success bool `json:"success"`
	}{
		true,
	}, http.StatusOK)
}

func handlerInternalJwtValidate(w *http.ResponseWriter, r *http.Request, matchIdStr string) bool {
	authValue := r.Header.Get("Authorization")

	token, err := validateJwtFormat(authValue)
	if err != nil {
		sendFailureResponse(w, err.Error(), http.StatusUnauthorized)
		return false
	}

	mId, err := jwtTokenValid(token)
	if err != nil {
		sendFailureResponse(w, "401 - Unauthorized, invalid token", http.StatusUnauthorized)
		return false
	}

	if string(mId) != matchIdStr {
		sendFailureResponse(w, "401 - Unauthorized, invalid matchId", http.StatusUnauthorized)
		return false
	}

	return true
}

func validateJwtFormat(authHeaderValue string) (string, error) {
	if authHeaderValue == "" {
		return "", errors.New("401 - Unauthorized, missing Authorization header")
	}

	authSplit := strings.Split(authHeaderValue, " ")
	if len(authSplit) != 2 {
		return "", errors.New("401 - Unauthorized, not in the 'Bearer <TOKEN>' format")
	}

	if authSplit[0] != "Bearer" {
		return "", errors.New("401 - Unauthorized, Bearer field not present")
	}

	return authSplit[1], nil
}

func jwtTokenValid(tokenString string) (MatchId, error) {
	// https://godoc.org/github.com/dgrijalva/jwt-go#Parse
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		// Don't forget to validate the alg is what you expect:
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}

		return []byte(viper.GetString("jwt-internal")), nil
	})

	if err != nil {
		return MatchId(""), errors.Wrap(err, "failed to parse jwt token")
	}

	if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
		if claims["aud"].(string) != "online-editor" {
			return MatchId(""), errors.New("invalid audience")
		}

		return MatchId(claims["matchId"].(string)), nil
	} else {
		return MatchId(""), errors.New("invalid token")
	}
}

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
