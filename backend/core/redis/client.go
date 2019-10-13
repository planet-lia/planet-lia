package redis

import (
	"fmt"
	_redis "github.com/go-redis/redis/v7"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
)

var (
	Client *_redis.Client
)

func NewClient() *_redis.Client {
	addr := fmt.Sprintf("%s:%d", viper.GetString("redis-host"), viper.GetInt("redis-port"))

	logging.Info("Connecting to redis", logrus.Fields{"address": addr})

	client := _redis.NewClient(&_redis.Options{
		Addr: addr,
		Password: viper.GetString("redis-password"),
		DB: viper.GetInt("redis-db"),
	})

	logging.Debug("Pinging Redis", logging.EmptyFields)
	_, err := client.Ping().Result()

	if err != nil {
		logging.Fatal("Failed to ping Redis", logrus.Fields{"error": err})
	}

	logging.Info("Successfully pinged Redis", logging.EmptyFields)

	return client
}

func ClientClose(c *_redis.Client) {
	if c == nil {
		logging.Info("Client is empty, doing nothing", logging.EmptyFields)
		return
	}

	err := c.Close()
	if err != nil {
		logging.Info("Failed to close Redis client", logrus.Fields{"error": err})
		return
	}

	logging.Info("Successfully closed Redis client connection", logging.EmptyFields)
}