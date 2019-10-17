package graphql

import "encoding/base64"

func isValidBase64EncodedString(s string) bool {
	_, err := base64.StdEncoding.DecodeString(s)
	return err == nil
}

func isValidBase64EncodedStringNonEmpty(s string) bool {
	if s == "" {
		return false
	}
	return isValidBase64EncodedString(s)
}
