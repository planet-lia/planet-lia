package internal

//
//import (
//	"encoding/json"
//	"fmt"
//	"io/ioutil"
//	"os"
//)
//
//type LiaConfig struct {
//	Language string `json:"language"`
//}
//
//func getConfig(botDir, path string) (*LiaConfig, error) {
//	configFile, err := ioutil.ReadFile(path)
//	if err != nil {
//		fmt.Fprintf(os.Stderr, "Error: Bot that you have provided does not seem like an actual bot. "+
//			"Please check if you have misspelled it or if it is missing the lia.json file. (bot dir = %s)\n", botDir)
//		fmt.Fprintf(os.Stderr, "Couldn't open file. Location: %s.", path)
//		return nil, err
//	}
//
//	cfg := &LiaConfig{}
//	if err := json.Unmarshal(configFile, cfg); err != nil {
//		fmt.Fprintf(os.Stderr, "Couldn't unmarshal lia config.")
//		return nil, err
//	}
//
//	return cfg, nil
//}
