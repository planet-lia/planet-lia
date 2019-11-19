package internal

import (
	"fmt"
	"github.com/gorilla/mux"
	"github.com/pkg/browser"
	"github.com/planet-lia/planet-lia/cli/internal/config"
	"net/http"
	"os"
	"path/filepath"
	"time"
)

func OpenMatchViewer(replayFile string, serverPort int) {
	r := mux.NewRouter()

	// Serve replays
	replayHandler := http.StripPrefix("/replays/", http.FileServer(http.Dir(config.PathToReplayFiles)))
	r.PathPrefix("/replays/").Handler(replayHandler)

	// Serve games folder
	gamesHandler := http.StripPrefix("/games/", http.FileServer(http.Dir(config.PathToGames)))
	r.PathPrefix("/games/").Handler(gamesHandler)

	// Serve local-match-viewer static and assets files
	staticHandler := http.StripPrefix("/static/", http.FileServer(http.Dir(filepath.Join(config.PathToMatchViewer, "static"))))
	r.PathPrefix("/static/").Handler(staticHandler)
	assetsHandler := http.StripPrefix("/assets/", http.FileServer(http.Dir(filepath.Join(config.PathToMatchViewer, "assets"))))
	r.PathPrefix("/assets/").Handler(assetsHandler)

	// If replayFile is provided and is not within replay/ directory, serve it to
	if replayFile != "" {
		r.PathPrefix("/selected-replay.json.gz").HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			http.ServeFile(w, r, replayFile)
		})
	}

	// Serve local-match-viewer
	r.PathPrefix("/").HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		http.ServeFile(w, r, filepath.Join(config.PathToMatchViewer, "index.html"))
	})

	// Run server
	go func() {
		address := fmt.Sprintf("127.0.0.1:%v", serverPort)
		srv := &http.Server{
			Handler:      r,
			Addr:         address,
			WriteTimeout: 15 * time.Second,
			ReadTimeout:  15 * time.Second,
		}
		fmt.Printf("Running match-viewer on %v.\n", address)
		if err := srv.ListenAndServe(); err != nil {
			fmt.Fprintf(os.Stderr, "failed to run server on port %d, try running using the -s flag to "+
				"run the server on a different port.\n%s\n", serverPort, err)
			os.Exit(1)
		}
	}()

	// Open specific replay file or file picker
	var url string
	if replayFile != "" {
		url = fmt.Sprintf("http://localhost:%d/viewer?replayUrl=/selected-replay.json.gz", serverPort)
	} else {
		url = fmt.Sprintf("http://localhost:%d/", serverPort)
	}
	fmt.Println("Opening a match viewer in browser.")
	if err := browser.OpenURL(url); err != nil {
		fmt.Fprintf(os.Stderr, "failed to open a browser for url %s\n%s\n", url, err)
		os.Exit(1)
	}

	// Wait a bit before displaying message how to quit since browser also
	// prints out some stuff when launched
	go func() {
		time.Sleep(time.Second)
		fmt.Println("Press Ctrl+C to exit...")
	}()

	// Wait for Ctrl+C
	select {}
}
