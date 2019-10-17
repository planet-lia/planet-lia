package onlineEditor

import (
	"context"
	"fmt"
	"github.com/pkg/errors"
	"github.com/planet-lia/planet-lia/backend/core/k8s"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"strconv"
	"time"
)

const (
	JobLabelMatchIdKey    = "k8s.planetlia.com/match-id"
	JobLabelTypeKey       = "k8s.planetlia.com/job-type"
	JobLabelTypeValue     = "online-editor-match"
	JobLabelCpuAllocation = "k8s.planetlia.com/cpu-allocation"
	noCpuForMatch         = 3    // TODO - do this dynamically
	noMemoryForMatch      = 2048 // 2 GiB of RAM
	oldJobCleanup         = 5 * time.Minute
)

// We use Kubernetes Pods instead of Jobs since they are more appropriate for our "try to run a match and if we fail
// then we fail" type of workloads. This means we have less housekeeping to do if a pod fails.

func Checks(c *kubernetes.Clientset) {
	namespace := viper.GetString("online-editor-k8s-namespace")
	logging.Debug("Checking if Kubernetes online editor namespace exists", logrus.Fields{"namespace": namespace})

	exists, err := k8s.CheckNamespaceExists(c, namespace)
	if err != nil {
		logging.Fatal("Failed to check if online editor namespace exists", logrus.Fields{"error": err,
			"namespace": namespace})
	}

	if exists {
		logging.Info("Kubernetes online editor namespace exists", logrus.Fields{"namespace": namespace})
	} else {
		if viper.GetBool("online-editor-k8s-namespace-create") {
			logging.Info("Creating online editor namespace for matches", logrus.Fields{"namespace": namespace})
			if err := k8s.CreateNamespace(c, namespace); err != nil {
				logging.Fatal("Failed to create online editor namespace for matches",
					logrus.Fields{"error": err, "namespace": namespace})
			}
		} else {
			logging.Fatal("Kubernetes online editor namespace for matches does not exists, please create it before running backend-core",
				logrus.Fields{"namespace": namespace})
		}
	}
}

func canSpawnMatch(ctx context.Context) (bool, error) {
	maxNoCpu := viper.GetInt("online-editor-max-cpu")
	currentNoCpu, err := noUsedCPU(ctx, k8s.Client)
	if err != nil {
		return false, errors.Wrap(err, "failed to check how many cpu are in usage")
	}

	return maxNoCpu-currentNoCpu >= noCpuForMatch, nil
}

func noUsedCPU(ctx context.Context, c *kubernetes.Clientset) (int, error) {
	namespace := viper.GetString("online-editor-k8s-namespace")

	lst, err := c.CoreV1().Pods(namespace).List(metav1.ListOptions{
		LabelSelector: fmt.Sprintf("%s=%s", JobLabelTypeKey, JobLabelTypeValue),
	})
	if err != nil {
		return 0, errors.Wrap(err, "failed to list online editor match jobs")
	}

	cpu := 0

	for _, job := range lst.Items {
		if jobCpu, ok := job.Labels[JobLabelCpuAllocation]; ok {
			jobCpuInt, err := strconv.Atoi(jobCpu)
			if err != nil {
				logging.WarningC(ctx, "Failed to convert cpu label value from string to int",
					logrus.Fields{"error": err, "strCpu": jobCpu, "jobName": job.Name})
			} else {
				cpu += jobCpuInt
			}
		}
	}

	return cpu, nil
}

func spawnNewMatchJob(ctx context.Context, client *kubernetes.Clientset, match Match) error {
	jwt, err := generateMatchJwtToken(ctx, match.Id)
	if err != nil {
		return errors.Wrap(err, "failed to generate match JWT token")
	}

	job := createJobSpecForMatch(ctx, match, jwt)

	namespace := viper.GetString("online-editor-k8s-namespace")
	_, err = client.CoreV1().Pods(namespace).Create(job)
	if err != nil {
		return errors.Wrap(err, "failed to create job")
	}

	return nil
}

func createJobSpecForMatch(ctx context.Context, match Match, jwt string) *corev1.Pod {
	activeDeadlineSeconds := int64(viper.GetDuration("online-editor-match-max-duration").Seconds())
	autoMountServiceAccountToken := false
	name := fmt.Sprintf("online-editor-match-job-%s", string(match.Id))
	resourceNoCpu := fmt.Sprintf("%d", noCpuForMatch)
	resourceNoMemory := fmt.Sprintf("%dMi", noMemoryForMatch)

	job := &corev1.Pod{
		ObjectMeta: metav1.ObjectMeta{
			Name: name,
			Labels: map[string]string{
				JobLabelTypeKey:       JobLabelTypeValue,
				JobLabelMatchIdKey:    string(match.Id),
				JobLabelCpuAllocation: strconv.Itoa(noCpuForMatch),
			},
		},
		Spec: corev1.PodSpec{
			Containers: []corev1.Container{{
				Name:  "online-editor",
				Image: viper.GetString("online-editor-image"),
				Args: []string{string(match.Id), "lia-1",
					match.Bots[0].Language, match.Bots[0].SourceUrl,
					match.Bots[1].Language, match.Bots[1].SourceUrl,
					"--root-backend-endpoint", viper.GetString("url"),
					"--jwt", jwt},
				Resources: corev1.ResourceRequirements{},
			}},
			RestartPolicy:                corev1.RestartPolicyNever,
			ActiveDeadlineSeconds:        &activeDeadlineSeconds,
			AutomountServiceAccountToken: &autoMountServiceAccountToken,
		},
	}

	if !viper.GetBool("online-editor-ignore-resource-requests") {
		job.Spec.Containers[0].Resources.Requests = corev1.ResourceList{
			corev1.ResourceName("cpu"):    resource.MustParse(resourceNoCpu),
			corev1.ResourceName("memory"): resource.MustParse(resourceNoMemory),
		}
	} else {
		logging.InfoC(ctx, "Ignoring online editor resource requests when generating pod spec", logging.EmptyFields)
	}

	if !viper.GetBool("online-editor-ignore-resource-limits") {
		job.Spec.Containers[0].Resources.Limits = corev1.ResourceList{
			corev1.ResourceName("cpu"):    resource.MustParse(resourceNoCpu),
			corev1.ResourceName("memory"): resource.MustParse(resourceNoMemory),
		}
	} else {
		logging.InfoC(ctx, "Ignoring online editor resource limits when generating pod spec", logging.EmptyFields)
	}

	return job
}

func oldJobs(client *kubernetes.Clientset, duration time.Duration) ([]corev1.Pod, error) {
	namespace := viper.GetString("online-editor-k8s-namespace")
	jobs, err := client.CoreV1().Pods(namespace).List(metav1.ListOptions{
		LabelSelector: fmt.Sprintf("%s=%s", JobLabelTypeKey, JobLabelTypeValue),
	})

	if err != nil {
		return []corev1.Pod{}, errors.Wrap(err, "failed to list all online editor jobs")
	}

	oldJobs := make([]corev1.Pod, 0)

	for _, job := range jobs.Items {
		if time.Now().Sub(job.CreationTimestamp.Time).Seconds() > duration.Seconds() || job.Status.Phase == corev1.PodSucceeded {
			oldJobs = append(oldJobs, job)
		}
	}

	return oldJobs, nil
}

func jobsWithErrors(client *kubernetes.Clientset) ([]corev1.Pod, error) {
	namespace := viper.GetString("online-editor-k8s-namespace")
	jobs, err := client.CoreV1().Pods(namespace).List(metav1.ListOptions{
		LabelSelector: fmt.Sprintf("%s=%s", JobLabelTypeKey, JobLabelTypeValue),
	})

	if err != nil {
		return []corev1.Pod{}, errors.Wrap(err, "failed to list all online editor jobs")
	}

	jobsWtErrors := make([]corev1.Pod, 0)

	for _, job := range jobs.Items {
		if job.Status.Phase == corev1.PodFailed {
			jobsWtErrors = append(jobsWtErrors, job)
		}
	}

	return jobsWtErrors, nil
}

func deleteJob(client *kubernetes.Clientset, jobName string) error {
	namespace := viper.GetString("online-editor-k8s-namespace")
	if err := client.CoreV1().Pods(namespace).Delete(jobName, &metav1.DeleteOptions{}); err != nil {
		return errors.Wrap(err, "failed to delete online editor jon")
	}
	return nil
}

func cleanupOldJobs(ctx context.Context, client *kubernetes.Clientset) error {
	jobs, err := oldJobs(client, oldJobCleanup)
	if err != nil {
		return errors.Wrap(err, "failed to get old jobs")
	}

	for _, job := range jobs {
		logging.InfoC(ctx, "Deleting old online editor match job", logrus.Fields{"jobName": job.Name})
		if err := deleteJob(client, job.Name); err != nil {
			logging.ErrorC(ctx, "Failed to delete online editor match job", logrus.Fields{"error": err, "jobName": job.Name})
		}
	}

	return nil
}

func cleanupJobsWithErrors(ctx context.Context, client *kubernetes.Clientset) error {
	jobs, err := jobsWithErrors(client)
	if err != nil {
		return errors.Wrap(err, "failed to get jobs with errors")
	}

	for _, job := range jobs {
		logging.InfoC(ctx, "Deleting online editor match job due to pod error", logrus.Fields{"jobName": job.Name})
		if err := deleteJob(client, job.Name); err != nil {
			logging.ErrorC(ctx, "Failed to delete online editor match job with status error", logrus.Fields{"error": err, "jobName": job.Name})
		}
	}

	return nil
}
