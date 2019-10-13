package onlineEditor

import (
	"github.com/pkg/errors"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/planet-lia/planet-lia/backend/core/redis"
	"github.com/sirupsen/logrus"
	"time"
)

// Checks the queue if there are any keys which have expired. If they have remove them from the queue.
func CollectGarbage() error {
	logging.Debug("Collecting garbage from the online editor queue", logging.EmptyFields)

	r := redis.Client.ZRange(queue, 0, -1)
	if r.Err() != nil {
		return errors.Wrap(r.Err(), "failed to get all values from queue")
	}

	garbage := 0

	for _, v := range r.Val() {
		r2 := redis.Client.Exists(submitDataKey(MatchId(v)))
		if r2.Err() != nil {
			return errors.Wrap(r2.Err(), "failed to check if match key exists")
		}

		if r2.Val() == 0 {
			// Key does not exist, let's remove it from the queue
			garbage++

			r3 := redis.Client.ZRem(queue, v)
			if r3.Err() != nil {
				return errors.Wrap(r3.Err(), "failed to remove garbage match key")
			}

			if r3.Val() > 0 {
				logging.Debug("Successfully removed garbage online editor match key from queue",
					logrus.Fields{"matchId": v})
			} else {
				logging.Warning("Failed to remove garbage online editor match key from queue",
				logrus.Fields{"matchId": v})
			}
		}
	}

	logging.Info("Finished collecting garbage from the online editor queue", logrus.Fields{"garbage": garbage})
	return nil
}

func GarbageCollector(interval time.Duration, shutdown chan bool) {
	logging.Info("Activating online editor garbage collector", logrus.Fields{"interval": interval.String()})

	ticker := time.NewTicker(interval)

	for {
		select {
		case <- shutdown:
			logging.Info("Shutting down garbage collector", logging.EmptyFields)
			ticker.Stop()
			return
		case <-ticker.C:
			if err := CollectGarbage(); err != nil {
				logging.Error("Collect garbage returned error", logrus.Fields{"error": err})
			}
		}
	}
}