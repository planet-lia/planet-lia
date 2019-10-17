package minio

import (
	"fmt"
	"github.com/minio/minio-go/v6"
	"github.com/pkg/errors"
	"github.com/planet-lia/planet-lia/backend/core/logging"
	"github.com/sirupsen/logrus"
)

func CreateBucket(c *minio.Client, name string, policy string) error {
	location := "us-east-1"

	if err := c.MakeBucket(name, location); err != nil {
		// Check to see if we already own this bucket (which happens if you run this twice)
		exists, errBucketExists := c.BucketExists(name)
		if errBucketExists == nil && exists {
			logging.Info("Bucket already exists", logrus.Fields{"bucket": name})
			return nil
		} else {
			return errors.Wrap(errBucketExists, "failed to check if bucket exists")
		}
	}

	logging.Info("Bucket created successfully", logrus.Fields{"bucket": name})

	if policy != "" {
		logging.Info("Setting bucket policy", logrus.Fields{"bucket": name})
		if err := c.SetBucketPolicy(name, policy); err != nil {
			return errors.Wrap(err, "failed to set policy on bucket")
		}
	}

	return nil
}

func InitialBuckets(c *minio.Client) error {
	logging.Info("Creating initial buckets if they don't exist", logging.EmptyFields)

	if err := CreateBucket(c, "replays", anonymousReadBucketPolicy("replays")); err != nil {
		return errors.Wrap(err, "failed to create replays bucket")
	}

	return nil
}

func anonymousReadBucketPolicy(bucket string) string {
	return fmt.Sprintf(`{
  "Id": "Policy1538401262451",
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "Stmt1538401259140",
      "Action": [
        "s3:GetObject"
      ],
      "Effect": "Allow",
      "Resource": "arn:aws:s3:::%s/*",
      "Principal": "*"
    }
  ]
}`, bucket)
}
