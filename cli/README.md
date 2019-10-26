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
chmod +x scrpits/build.sh
./scripts/build.sh
```

 
#### Custom Releases File

In the terminal where you will be running the tool you need to export environment variable `RELEASES_URL`` with 
url to your self hosted releases file.

```bash
export RELEASES_URL="http://127.0.0.1:8000/planet-lia-releases.json"
```

