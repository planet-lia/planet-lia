package minio

import (
	"github.com/minio/minio-go/v6"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	"net"
	"strconv"
)

var (
	Client *minio.Client
)

func NewClient() *minio.Client {
	host := viper.GetString("minio-host")
	port := viper.GetInt("minio-port")
	endpoint := net.JoinHostPort(host, strconv.Itoa(port))

	accessKey := viper.GetString("minio-access-key")
	secretKey := viper.GetString("minio-secret-key")
	useSSL := viper.GetBool("minio-ssl")

	client, err := minio.New(endpoint, accessKey, secretKey, useSSL)
	if err != nil {
		logging.Fatal("Failed to create Minio client", logrus.Fields{"error": err})
	}

	return client
}

func GetEndpoint() string {
	host := viper.GetString("minio-host")
	port := viper.GetInt("minio-port")
	endpoint := net.JoinHostPort(host, strconv.Itoa(port))

	useSSL := viper.GetBool("minio-ssl")

	if useSSL {
		endpoint = "https://" + endpoint
	} else {
		endpoint = "http://" + endpoint
	}

	return endpoint
}