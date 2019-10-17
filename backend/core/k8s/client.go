package k8s

import (
	"github.com/pkg/errors"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
)

var (
	Client *kubernetes.Clientset
)

func NewClient() (*kubernetes.Clientset, error) {
	kubeconfigFilePath := viper.GetString("k8s-kubeconfig")

	var configK8s *rest.Config
	var err error

	if kubeconfigFilePath == "" {
		logging.Info("Using Kubernetes in-cluster client configuration", logging.EmptyFields)
		configK8s, err = connectInCluster()
		if err != nil {
			logging.Warning("Did you mean to connect to Kubernetes out-cluster? Use --k8s-kubeconfig flag.", logging.EmptyFields)
			return nil, err
		}
	} else {
		logging.Info("Using Kubernetes out-cluster client configuration", logrus.Fields{"kubeconfig": kubeconfigFilePath})
		configK8s, err = connectOutCluster(kubeconfigFilePath)
		if err != nil {
			return nil, err
		}
	}

	client, err := kubernetes.NewForConfig(configK8s)
	if err != nil {
		return nil, errors.Wrap(err, "failed to create new client")
	}

	return client, nil
}

// Create in-cluster config
func connectInCluster() (*rest.Config, error) {
	configK8s, err := rest.InClusterConfig()
	if err != nil {
		return nil, errors.Wrap(err, "Failed to connect to Kubernetes from inside the cluster")
	}
	return configK8s, nil
}

// Create out-cluster config
func connectOutCluster(kubeconfigFilePath string) (*rest.Config, error) {
	// Use the current context in kubeconfig
	configK8s, err := clientcmd.BuildConfigFromFlags("", kubeconfigFilePath)
	if err != nil {
		return nil, errors.Wrap(err, "failed to connect to Kubernetes from outside the cluster")
	}

	return configK8s, nil
}
