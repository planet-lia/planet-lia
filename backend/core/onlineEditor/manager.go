package onlineEditor

import (
	"context"
	"github.com/pkg/errors"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/planet-lia/planet-lia/backend/core/redis"
	"github.com/sirupsen/logrus"
	"k8s.io/client-go/kubernetes"
	"time"
)

const (
	garbageCollectorInterval = 3 * time.Second
)

func ManagerStart(client *kubernetes.Clientset) {
	ctx := context.TODO()

	for {
		time.Sleep(3 * time.Second)

		canSpawn, err := canSpawnMatch(ctx)
		if err != nil {
			logging.ErrorC(ctx, "Failed to check if we can spawn match", logrus.Fields{"error": err})
			continue
		}

		if !canSpawn {
			logging.DebugC(ctx, "Cannot spawn new online editor match due to insufficient resources", logging.EmptyFields)
			continue
		}

		// We can spawn a new match!
		match, err := getNextMatch(ctx)
		if err != nil {
			logging.ErrorC(ctx, "Failed to get next online editor match", logrus.Fields{"error": err})
		}

		if err = spawnNewMatchJob(ctx, client, match); err != nil {
			logging.ErrorC(ctx, "Failed to spawn new online editor match", logrus.Fields{"error": err, "matchId": match.Id})

			if err2 := SetMatchData(ctx, match.Id, MatchStatusFailure, "", ""); err2 != nil {
				logging.ErrorC(ctx, "Failed to set online editor match as failure when we failed to spawn the match",
					logrus.Fields{"error": err, "matchId": match.Id})
			}

			continue
		}

	}
}

func ManagerGarbageCollectorStart(client *kubernetes.Clientset, shutdown <-chan bool) {
	ctx := context.TODO()

	logging.InfoC(ctx, "Starting online editor manager garbage collector", logging.EmptyFields)
	ticker := time.NewTicker(garbageCollectorInterval)

	for {
		select {
		case <-shutdown:
			logging.InfoC(ctx,"Shutting down online editor garbage collector", logging.EmptyFields)
			ticker.Stop()
			return
		case <-ticker.C:
			logging.DebugC(ctx, "Running online editor garbage collector", logging.EmptyFields)

			if err := cleanupOldJobs(ctx, client); err != nil {
				logging.ErrorC(ctx, "Failed to cleanup old jobs", logrus.Fields{"error": err})
			}

			if err := cleanupJobsWithErrors(ctx, client); err != nil {
				logging.ErrorC(ctx, "Failed to cleanup jobs with error status", logrus.Fields{"error": err})
			}

		}
	}
}

// Call will block indefinitely waiting for the next match in the queue
func getNextMatch(ctx context.Context) (Match, error) {
	if err := redis.Client.Ping().Err(); err != nil {
		return Match{}, errors.Wrap(err, "redis client is down")
	}

	logging.InfoC(ctx, "Waiting for next match item from queue", logging.EmptyFields)
	res := redis.Client.BZPopMin(time.Duration(0), queue)
	if res.Err() != nil {
		return Match{}, errors.Wrap(res.Err(), "failed to pop next match item from queue")
	}

	matchId := MatchId(res.Val().Member.(string))
	m, err := GetMatch(ctx, matchId)
	if err != nil {
		return Match{}, errors.Wrap(err, "failed to get match data")
	}

	err = ExtendExpirationMatchState(ctx, matchId, time.Minute * 10)
	if err != nil {
		return Match{}, errors.Wrap(err, "failed to disable match expiration")
	}

	return m, nil
}
