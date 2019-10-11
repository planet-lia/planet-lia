# Match Viewer
A match viewer that can replay matches for Planet Lia games or any kind of 2D game.
The only requirement is to store the match state in a replay file format documented in [How to Write Replay Files](#how-to-write-replay-files) guide.
It is very simple, really flexible and it provides many cool features such as changing speed when playing a replay, jumping freely in time, displaying charts with statistics and much more.

![Match Viewer](docs/images/match-viewer.png)

## How to Write Replay Files
Are you developing your own game for Planet Lia or are you just interested in how it works?

[CHECK THE GUIDE.](docs/writing_replay_files.md)

## Run
1. `git clone REPO_URL`
2. `cd games-utils/match-viewer/`
3. `npm install`
4. `python3 serve_games_assets.py ../../games/` 
    * This will serve assets from `games/` directory at port `3333` which is used in `src/App.tsx` file by default. 
    It will also allow CORS for easier development.
5. `npm start`
6. Visit http://localhost:3000/

## Test
Run all tests with `npm test -- src`. 
To run with coverage run `npm test -- src --coverage`.
