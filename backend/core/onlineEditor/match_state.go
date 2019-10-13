package onlineEditor

import (
	"context"
	"fmt"
	"github.com/pkg/errors"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/planet-lia/planet-lia/backend/core/redis"
	"github.com/sirupsen/logrus"
	"regexp"
	"strconv"
	"time"
)

const (
	MatchStatusQueued = "queued"
	MatchStatusGenerating = "generating"
	MatchStatusSuccess = "success"
	MatchStatusFailure = "failure"
)

type Match struct {
	Id MatchId
	Game string
	QueuePosition int
	Bots []InputBot
	Log string
	Status string
	Created time.Time
}

type MatchDoesNotExist struct{}
func (_ MatchDoesNotExist) Error() string {
	return "match does not exist"
}

func GetMatchState(ctx context.Context, id MatchId) (Match, error) {
	exists, err := MatchExists(id)

	if err != nil {
		logging.ErrorC(ctx, "Failed to check if match exists", logrus.Fields{"error": err})
		return Match{}, errors.New("failed to get match data")
	}

	if !exists {
		return Match{}, MatchDoesNotExist{}
	}

	m, err := GetMatch(ctx, id)
	if err != nil {
		logging.ErrorC(ctx, "Failed to get online editor match data", logrus.Fields{"error": err})
		return Match{}, err
	}

	return m, nil
}


func MatchExists(id MatchId) (bool, error) {
	r := redis.Client.Exists(submitDataKey(id))
	if r.Err() != nil {
		return false, r.Err()
	}

	return r.Val() > 0, nil
}

type MatchStateFailed struct {}
func (_ MatchStateFailed) Error() string {
	return "failed to get match data"
}

func GetMatch(ctx context.Context, id MatchId) (Match, error) {
	logging.InfoC(ctx, "Getting online editor match", logrus.Fields{"matchId": id})

	r := redis.Client.HGetAll(submitDataKey(id))
	if r.Err() != nil {
		return Match{}, r.Err()
	}

	data, err := r.Result()
	if err != nil {
		return Match{}, err
	}

	match := Match{}

	match.Id = id
	if val, ok := data["game"]; ok {
		match.Game = val
	} else {
		logging.ErrorC(ctx, "Failed to find online editor match key: game", logrus.Fields{"matchId": id})
		return Match{}, MatchStateFailed{}
	}

	bots, err := MatchBots(ctx, id)
	if err != nil {
		logging.ErrorC(ctx, "Failed to get online editor match bots", logrus.Fields{"matchId": id, "error": err})
		return Match{}, MatchStateFailed{}
	}
	match.Bots = bots

	if val, ok := data["log"]; ok {
		match.Log = val
	} else {
		logging.ErrorC(ctx, "Failed to find online editor match key: log", logrus.Fields{"matchId": id})
		return Match{}, MatchStateFailed{}
	}

	if val, ok := data["created"]; ok {
		t, err := unixNanoStringToTime(val)
		if err != nil {
			logging.ErrorC(ctx, "Failed to parse the time", logrus.Fields{"error": err, "matchId": id})
			return Match{}, MatchStateFailed{}
		}

		match.Created = t
	} else {
		logging.ErrorC(ctx, "Failed to find online editor match key: created", logrus.Fields{"matchId": id})
		return Match{}, MatchStateFailed{}
	}

	if val, ok := data["status"]; ok {
		match.Status = val
	} else {
		logging.ErrorC(ctx, "Failed to find online editor match key: status", logrus.Fields{"matchId": id})
		return Match{}, MatchStateFailed{}
	}

	if match.Status == MatchStatusQueued {
		pos, err := matchQueuePlace(ctx, id)
		if err != nil {
			logging.ErrorC(ctx, "Cannot calculate match queue place for online editor match",
				logrus.Fields{"matchId": id, "error": err})
			match.QueuePosition = -1
		} else {
			match.QueuePosition = pos
		}
	}

	return match, nil
}

func ExtendExpirationMatchState(ctx context.Context, id MatchId) error {
	key := submitDataKey(id)
	logging.InfoC(ctx, "Extending the expiration of online editor match key", logrus.Fields{"matchId": id, "key": key})

	r := redis.Client.TTL(key)
	if r.Err() != nil {
		return errors.Wrap(r.Err(), "failed to get TTL for key")
	}

	r2 := redis.Client.Expire(submitDataKey(id), submitDefaultTTL)
	if r2.Err() != nil {
		return errors.Wrap(r2.Err(), "failed to set exiration for key")
	}

	return nil
}

func DisableExpirationMatchState(ctx context.Context, id MatchId) error {
	key := submitDataKey(id)
	logging.InfoC(ctx, "Disabling expiration of online editor match key", logrus.Fields{"matchId": id, "key": key})

	r := redis.Client.Persist(submitDataKey(id))
	if r.Err() != nil {
		return errors.Wrap(r.Err(), "failed to persist key")
	}

	return nil
}

func GetMatchBot(ctx context.Context, id MatchId, bot string) (string, error) {
	logging.InfoC(ctx, "Getting online editor match bot", logrus.Fields{"matchId": id, "bot": bot})

	r := redis.Client.HGet(submitDataKey(id), bot + ".source")
	if r.Err() != nil {
		return "", errors.Wrap(r.Err(), "failed to get bot field")
	}

	data, err := r.Result()
	if err != nil {
		return "", errors.Wrap(err, "failed to get bot field from result")
	}

	return data, nil
}

func unixNanoStringToTime(unixNano string) (time.Time, error) {
	val, err := strconv.ParseInt(unixNano, 10, 64)
	if err != nil {
		return time.Time{}, errors.New("string is not an integer")
	}

	return time.Unix(0, val), nil
}

func matchQueuePlace(ctx context.Context, id MatchId) (int, error) {
	r := redis.Client.ZRank(queue, string(id))
	if r.Err() != nil {
		return 0, errors.Wrap(r.Err(), "Failed to get rank of queue item for matchQueuePlace")
	}

	score := r.Val()

	r = redis.Client.ZCount(queue, fmt.Sprintf("%d", score), "+inf")
	if r.Err() != nil {
		return 0, errors.Wrap(r.Err(), "Failed to get count of queue for matchQueuePlace")
	}

	return int(r.Val()), nil
}

func SetMatchData(ctx context.Context, id MatchId, status, log, replayUrl string) error {
	logging.InfoC(ctx, "Setting online editor match data", logrus.Fields{"matchId": id})

	data := make(map[string]interface{})

	data["status"] = status
	data["log"] = log

	if replayUrl != "" {
		data["replayUrl"] = replayUrl
	}

	r := redis.Client.HMSet(submitDataKey(id), data)
	if r.Err() != nil {
		return errors.Wrap(r.Err(), "failed to update match data")
	}

	return nil
}

var redisBotsList, _ = regexp.Compile("bots.\\d*.(source|language)")

func MatchBots(ctx context.Context, id MatchId) ([]InputBot, error) {
	rKeys := redis.Client.HKeys(submitDataKey(id))
	if rKeys.Err() != nil {
		return []InputBot{}, errors.Wrap(rKeys.Err(), "failed to get HashMap keys")
	}

	count := 0
	for _, k := range rKeys.Val() {
		if redisBotsList.MatchString(k) {
			count += 1
		}
	}

	totalNoBots := count / 2
	if count % 2 != 0 {
		logging.WarningC(ctx, "Non even number of fields for bots list", logrus.Fields{"matchId": id})
	}

	botsList := make([]InputBot, totalNoBots)
	for i := 0; i < totalNoBots; i++ {
		sourceKey := fmt.Sprintf("bots.%d.source", i)
		langKey := fmt.Sprintf("bots.%d.language", i)

		botsList[i].Name = fmt.Sprintf("bots.%d", i)

		r := redis.Client.HGet(submitDataKey(id), sourceKey)
		if r.Err() != nil {
			return []InputBot{}, errors.Wrap(r.Err(), fmt.Sprintf("failed to get bot: %d source", i))
		}

		botsList[i].Source = r.Val()


		r = redis.Client.HGet(submitDataKey(id), langKey)
		if r.Err() != nil {
			return []InputBot{}, errors.Wrap(r.Err(), fmt.Sprintf("failed to get bot: %d language", i))
		}

		botsList[i].Language = r.Val()
	}

	return botsList, nil
}