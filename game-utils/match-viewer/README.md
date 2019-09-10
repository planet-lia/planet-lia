# Match viewer

![Match Viewer](docs/images/match-viewer.png)

A match viewer that can play matches stored in Planet Lia replay files.
It can play any match for any type of game stored in the corresponding format.

## How to write replay files

Are you developing your own game for Planet Lia or are you just interested in how it works?

[CHECK THE GUIDE.](docs/writing_replay_files.md)

## Run

1. `git clone REPO_URL`
2. `cd games-utils/match-viewer/`
3. `npm install`
4. `python3 serve_games_assets.py ../../games/ 3333` 
    * This will serve assets from `games/` directory at port `3333` which is used in `src/App.tsx` file by default. 
    It will also allow CORS for easier development.
5. `npm start`
6. Visit http://localhost:3000/

## Test
Run all tests with `npm test -- src`. 
To run with coverage run `npm test -- src --coverage`.