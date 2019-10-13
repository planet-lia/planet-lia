package server

import (
	"bytes"
	"context"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/satori/go.uuid"
	"github.com/sirupsen/logrus"
	"io/ioutil"
	"net/http"
	"time"
)

func loggingMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		t1 := time.Now()

		logging.InfoR(r, "Request received", logrus.Fields{
			"method":        r.Method,
			"url":           r.RequestURI,
			"requestLength": r.ContentLength,
		})

		next.ServeHTTP(w, r)

		duration := time.Since(t1)

		// TODO - check if an error occurred during handling of request and log it
		logging.InfoR(r, "Request serviced", logrus.Fields{
			"method":        r.Method,
			"url":           r.RequestURI,
			"requestLength": r.ContentLength,
			"duration":      duration,
			"durationSec":   duration.Seconds(),
		})

	})
}

// Add the request-id field to the request and response header as well as the the context under
// the key: "requestId".
func requestIdMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		reqId := uuid.NewV4().String()
		r.Header.Add(logging.HttpHeaderRequestIdField, reqId)
		w.Header().Set(logging.HttpHeaderRequestIdField, reqId)

		next.ServeHTTP(w, r.WithContext(context.WithValue(r.Context(), "requestId", reqId)))
	})
}

// Log the GraphQL queries
func graphqlMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {

		// If we read directly from the request body buffer, the next call will not
		// be able to read the contents. So we make two buffers and attach one back
		// to the request and use the other one ourselves.
		buf, _ := ioutil.ReadAll(r.Body)
		reader1 := ioutil.NopCloser(bytes.NewBuffer(buf))
		reader2 := ioutil.NopCloser(bytes.NewBuffer(buf))

		r.Body = reader2

		bodyRaw, err := ioutil.ReadAll(reader1)
		if err != nil {
			logging.ErrorC(r.Context(), "Failed to read request body", logrus.Fields{"error": err})
			sendFailureResponse(&w, "internal server error", http.StatusInternalServerError)
			return
		}
		r.Body.Close()

		logging.InfoC(r.Context(), "Log query", logrus.Fields{"query": string(bodyRaw)})

		next.ServeHTTP(w, r)
	})
}
