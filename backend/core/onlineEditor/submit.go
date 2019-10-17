package onlineEditor

import (
	"context"
	"errors"
	"fmt"
	_redis "github.com/go-redis/redis/v7"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/planet-lia/planet-lia/backend/core/redis"
	uuid "github.com/satori/go.uuid"
	"github.com/sirupsen/logrus"
	"time"
)

type MatchId string

const queue = "q:online-editor"
const submitPrefix = "online-editor-session:"

const SubmitDefaultTTL = time.Second * 5

type InputBot struct {
	Name      string
	Language  string
	Source    string
	SourceUrl string
}

func Submit(ctx context.Context, game string, bots []InputBot) (MatchId, error) {
	mid := generateMatchId()
	key := submitDataKey(mid)

	logging.DebugC(ctx, "Generated matchId", logrus.Fields{"matchId": mid})

	now := time.Now()

	data := make(map[string]interface{})

	data["game"] = game
	data["log"] = ""
	data["replayUrl"] = ""
	data["created"] = now.UnixNano()
	data["status"] = MatchStatusQueued

	for i, b := range bots {
		data[fmt.Sprintf("bots.%d.source", i)] = b.Source
		data[fmt.Sprintf("bots.%d.language", i)] = b.Language
	}

	redis.Client.HMSet(key, data)
	redis.Client.Expire(key, SubmitDefaultTTL)

	logging.InfoC(ctx, "Created HM for online editor match details",
		logrus.Fields{"ttl": SubmitDefaultTTL, "key": key, "game": game, "noBots": len(bots)})

	m := _redis.Z{
		Score:  float64(queueScore(now)),
		Member: string(mid),
	}

	r := redis.Client.ZAdd(queue, &m)

	if r.Err() != nil {
		logging.ErrorC(ctx, "Failed to add to online editor queue", logrus.Fields{"error": r.Err()})
		return mid, errors.New("failed to add to queue")
	}

	logging.DebugC(ctx, "Added to online editor queue match", logrus.Fields{"matchId": mid})

	return mid, nil
}

func generateMatchId() MatchId {
	id := uuid.NewV4().String()
	return MatchId(id)
}

func submitDataKey(id MatchId) string {
	return submitPrefix + string(id)
}

// We assign a score based on the current time. This is a typical FIFO scoring. We take a modified version
// of Unix time to be the score. We use the nanoseconds version for better precision (instead of second precision),
// however since the value is too large for Redis' sorted set max score value we remove the bottom 4 digits.
// This technique is going to work until the year 2255 - see Redis sorted set max score
func queueScore(t time.Time) uint64 {
	return uint64(t.UnixNano() / 1000)
}

func queueScoreString(t uint64) string {
	return fmt.Sprintf("%d", t)
}
