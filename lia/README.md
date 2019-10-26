# Lia-CLI

Download latest release [here](https://github.com/planet-lia/planet-lia/cli/releases).


## Lia CLI
```bash
lia is a CLI tool for easier development of Lia bots.

Usage:
  lia [flags]
  lia [command]

Available Commands:
  account     Check which user is currently logged into Lia-SDK
  bot         Create new bot
  compile     Compiles/prepares bot in specified dir
  fetch       Fetches a bot from url and sets a new name
  generate    Generates a game
  help        Help about any command
  login       Login to Lia with your account
  logout      Logout from Lia
  play        Compiles and generates a game between bot1 and bot2
  replay      Runs a replay viewer
  settings    Views the user's settings
  update      Updates Lia-SDK
  upload      Uploads the bot to Lia leaderboard

Flags:
  -h, --help        help for lia
  -l, --languages   show all supported languages
  -v, --version     show tools version

Use "lia [command] --help" for more information about a command.
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

## Analytics 
To change your status to a tester in google analytics,
find the .lia.json file in your home directory. There you should add a line
so it looks someting like this.

```json
{
  "analyticsallow": true,
  "analyticsallowedversion": "sdk-version",
  "trackingid": "your-tracking-id",
  "testing": true
}
```

## Automatic update testing

This are the instructions on how to test the automatic update locally. 
Do this every time before publishing new version to se if  automatic update works.
 
#### 1. Serving new releases 

We will use python3 to serve our releases.

* Create a hierarchy like the one below somewhere on your disk:
```bash
├── download
│   └── vX.Y.Z # Replace with your new version
│       ├── lia-sdk-linux.zip # This is your new linux build
│       ├── lia-sdk-macos.zip # This is your new macos build
│       └── lia-sdk-windows.zip # This is your new windows build
└── latest
``` 
* In `latest` file paste `{"tag_name": "vX.Y.Z"}` again replacing `vX.Y.Z` with new version.
* Run the server with `python -m SimpleHTTPServer 5000` from the root of the hierarchy created above.

#### 2. Using local server in Lia-SDK

In the terminal where you will be running your update you need to export the base URL to your releases.

```bash
export RELEASES_BASE_URL="http://127.0.0.1:5000/"
```


#### 3. Run update

```bash
./lia update
```

The current Lia-SDK should now be updated to the new one. 
Test if everything works as expected.

## Specifying Lia backend 
You can choose to which Lia backend you will connect. 
This is managed through the following environment variable (eg. for dev backend):
```bash
export LIA_BACKEND_URL="https://dev.cloud1.liagame.com"
```