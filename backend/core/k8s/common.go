package k8s

import (
	"github.com/pkg/errors"
	_errors "k8s.io/apimachinery/pkg/api/errors"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	corev1 "k8s.io/api/core/v1"
)

func CheckNamespaceExists(client *kubernetes.Clientset, namespace string) (bool, error) {
	_, err := client.CoreV1().Namespaces().Get(namespace, v1.GetOptions{})
	if _errors.IsNotFound(err) {
		return false, nil
	} else if err != nil {
		return false, errors.Wrap(err, "failed to fetch namespace")
	}

	return true, nil
}

func CreateNamespace(client *kubernetes.Clientset, namespace string) error {
	ns := corev1.Namespace{
		ObjectMeta: v1.ObjectMeta{
			Name: namespace,
		},
	}
	_, err := client.CoreV1().Namespaces().Create(&ns)

	if err != nil {
		return errors.Wrap(err, "failed to create namespace")
	}

	return nil
}