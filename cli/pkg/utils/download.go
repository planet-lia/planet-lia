package utils

import (
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"os"
	"time"
)

func DownloadFile(url string, outputFile *os.File, timeout int) error {
	fmt.Printf("Downloading file from: %s\n", url)

	var client = &http.Client{
		Timeout: time.Second * time.Duration(timeout),
	}

	resp, err := client.Get(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != 200 {
		body, _ := ioutil.ReadAll(resp.Body)
		return fmt.Errorf("failed to download a file, status code: %v\n%v\n", resp.StatusCode, string(body))
	}

	// Write the body to file
	if _, err = io.Copy(outputFile, resp.Body); err != nil {
		return err
	}

	fmt.Printf("Successfully downloaded file from '%s' to '%s'.\n", url, outputFile.Name())

	return nil
}
