package graphql

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestIsValidBase64EncodedString(t *testing.T) {
	assert := assert.New(t)

	assert.Equal(true, isValidBase64EncodedString("cHJpbnQoIkhlbGxvIHdvcmxkISIp"), "")
	assert.Equal(false, isValidBase64EncodedString("cHJpbnQoIkhl=GxvIHdvcmxkISIp"), "")
	assert.Equal(false, isValidBase64EncodedString("fdajfjlkfjeljafi8383#88383"), "")

	assert.Equal(true, isValidBase64EncodedString(""), "")
}

func TestIsValidBase64EncodedStringNonEmpty(t *testing.T) {
	assert := assert.New(t)

	assert.Equal(true, isValidBase64EncodedStringNonEmpty("cHJpbnQoIkhlbGxvIHdvcmxkISIp"), "")
	assert.Equal(false, isValidBase64EncodedStringNonEmpty(""), "")
}
