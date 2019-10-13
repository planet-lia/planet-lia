package onlineEditor

import (
	"context"
	"github.com/dgrijalva/jwt-go"
	"github.com/pkg/errors"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	"time"
)

func generateMatchJwtToken(ctx context.Context, id MatchId) (string, error) {
	logging.InfoC(ctx, "Generating internal JWT match token", logrus.Fields{"matchId": id})

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"matchId": string(id),
		"aud": "online-editor",
		"iat": time.Now().Unix(),
		"iss": "backend-core",
	})

	tokenString, err := token.SignedString([]byte(viper.GetString("jwt-internal")))
	if err != nil {
		return "", errors.Wrap(err, "failed to sign token")
	}

	return tokenString, nil
}
