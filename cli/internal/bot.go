package internal

//
//import (
//	"fmt"
//	"github.com/planet-lia/planet-lia/cli"
//	"github.com/planet-lia/planet-lia/cli/internal/config"
//	"os"
//)
//
//func FetchBotByLanguage(lang string, name string) {
//	// Check if the bot with name already exists
//	if isUsed, err := isNameUsed(name); err != nil {
//		fmt.Fprintf(os.Stderr, "failed to check if name isUsed. %s", err)
//		os.Exit(lia_SDK.Generic)
//	} else if isUsed {
//		fmt.Fprintf(os.Stderr, "bot name %s already exists. Choose another name.\n", name)
//		os.Exit(lia_SDK.BotExists)
//	}
//
//	// Fetch repository url for specified language
//	langData, err := getRepositoryURL(lang)
//	if err != nil {
//		fmt.Fprintln(os.Stderr, err)
//		os.Exit(lia_SDK.FailedToFindRepo)
//	}
//
//	FetchBot(langData.BotURL, name, "")
//}
//
//// Find repository from config file based on lang parameter
//func getRepositoryURL(lang string) (*config.Language, error) {
//	for _, langData := range config.Cfg.Languages {
//		if lang == langData.Name {
//			return &langData, nil
//		}
//	}
//
//	return nil, fmt.Errorf("BotRepo not found: %v. Use one of the supported languages.\n", lang)
//}
