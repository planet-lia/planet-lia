# Planet Lia - CLI

The CLI tool for Planet Lia platform.
The tool is called `lia`.

## Planet Lia CLI
```bash
TODO paste the result of --help command
```


## Development
### Dependencies
Install all dependencies and update them by cd-ing into the repository
root and running:
```bash
go get -u ./...
```

### Build
```bash
./scripts/build.sh
```

If you want to run build without testing and redeploying local-match-viewer run `./scripts/build.sh quick`.

 
#### Serve Releases Locally

Run the command below. It will build a match-generator and java, kotlin and python3 bots for that game 
and serve them as a release on port `4447`. 

```bash
./scripts/serveReleases.sh [game-name] 
# eg.  ./scripts/serveReleases.sh planetization
```

Also expose the url on which releases are server: `export RELEASES_URL="http://localhost:4447/planet-lia-releases.json"`
