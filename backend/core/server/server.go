package server

import (
	"context"
	"github.com/gorilla/mux"
	"github.com/graphql-go/handler"
	"github.com/planet-lia/planet-lia/backend/core/graphql"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	"net"
	"net/http"
	"time"
)

const (
	serverWriteTimeout = 30 * time.Second
	serverReadTimeout  = 30 * time.Second
)

func Start(shutdown chan bool) {

	graphqlHandler := handler.New(&handler.Config{
		Schema:   &graphql.Schema,
		Pretty:   true,
		GraphiQL: viper.GetBool("graphiql"),
	})

	r := mux.NewRouter()
	r.StrictSlash(true) // accepts requests with trailing slash

	r.HandleFunc("/", rootHandler).Methods("GET")
	r.HandleFunc("/health", healthHandler).Methods("GET")
	r.Handle("/graphql", graphqlMiddleware(graphqlHandler))

	r.NotFoundHandler = http.HandlerFunc(notFoundHandler)

	r.Use(requestIdMiddleware)
	r.Use(loggingMiddleware)

	bind := net.JoinHostPort(viper.GetString("http-bind"), viper.GetString("port"))

	srv := &http.Server{
		Handler:      r,
		Addr:         bind,
		WriteTimeout: serverWriteTimeout,
		ReadTimeout:  serverReadTimeout,
	}

	logrus.WithFields(logrus.Fields{"bind": bind}).Info("Starting HTTP server")

	go func() {
		logrus.Fatal(srv.ListenAndServe())
	}()

	<-shutdown
	if err := srv.Shutdown(context.TODO()); err != nil {
		logrus.WithError(err).Fatal("Failed to shutdown HTTP server")
	}
}
