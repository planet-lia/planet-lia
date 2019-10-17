package logging

import (
	"context"
	"github.com/sirupsen/logrus"
	"net/http"
)

// Wrapper for logrus that attaches the requestId if present to logrus's fields.
const HttpHeaderRequestIdField = "X-Request-Id"

var EmptyFields = logrus.Fields{}

func getRequestId(r *http.Request) string {
	return r.Header.Get(HttpHeaderRequestIdField)
}

func addRequestId(r *http.Request, attr *logrus.Fields) {
	reqId := getRequestId(r)
	if reqId != "" {
		(*attr)["requestId"] = reqId
	}
}

func DebugR(r *http.Request, message string, attr logrus.Fields) {
	addRequestId(r, &attr)
	logrus.WithFields(attr).Debug(message)
}

func InfoR(r *http.Request, message string, attr logrus.Fields) {
	addRequestId(r, &attr)
	logrus.WithFields(attr).Info(message)
}

func WarningR(r *http.Request, message string, attr logrus.Fields) {
	addRequestId(r, &attr)
	logrus.WithFields(attr).Warning(message)
}

func ErrorR(r *http.Request, message string, attr logrus.Fields) {
	addRequestId(r, &attr)
	logrus.WithFields(attr).Error(message)
}

func FatalR(r *http.Request, message string, attr logrus.Fields) {
	addRequestId(r, &attr)
	logrus.WithFields(attr).Fatal(message)
}

func addRequestIdFromContext(ctx context.Context, attr *logrus.Fields) {
	reqId := ctx.Value("requestId")
	if reqId != nil {
		(*attr)["requestId"] = reqId
	}
}

func DebugC(ctx context.Context, message string, attr logrus.Fields) {
	addRequestIdFromContext(ctx, &attr)
	logrus.WithFields(attr).Debug(message)
}

func InfoC(ctx context.Context, message string, attr logrus.Fields) {
	addRequestIdFromContext(ctx, &attr)
	logrus.WithFields(attr).Info(message)
}

func WarningC(ctx context.Context, message string, attr logrus.Fields) {
	addRequestIdFromContext(ctx, &attr)
	logrus.WithFields(attr).Warning(message)
}

func ErrorC(ctx context.Context, message string, attr logrus.Fields) {
	addRequestIdFromContext(ctx, &attr)
	logrus.WithFields(attr).Error(message)
}

func FatalC(ctx context.Context, message string, attr logrus.Fields) {
	addRequestIdFromContext(ctx, &attr)
	logrus.WithFields(attr).Fatal(message)
}

func Debug(message string, attr logrus.Fields) {
	logrus.WithFields(attr).Debug(message)
}

func Info(message string, attr logrus.Fields) {
	logrus.WithFields(attr).Info(message)
}

func Warning(message string, attr logrus.Fields) {
	logrus.WithFields(attr).Warning(message)
}

func Error(message string, attr logrus.Fields) {
	logrus.WithFields(attr).Error(message)
}

func Fatal(message string, attr logrus.Fields) {
	logrus.WithFields(attr).Fatal(message)
}
