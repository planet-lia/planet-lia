# Local Match Viewer

A wrapper around `game-utils/match-viewer` react project that serves as a local match viewer.

## Run
1. `git clone REPO_URL`
2. `cd websites/local-match-viewer/`
3. `make install`
4. Serve some replay files: `python3 ../../game-utils/match-viewer/serve_games_assets.py -p 4444 ../../games/lia-1
/assets/1.0/`
5. Serve assets: `python3 ../../game-utils/match-viewer/serve_games_assets.py ../../games/`
6. `make dev`
7. Visit http://localhost:3000/

## Test
Run all tests with `npm test Y-- src`. 
To run with coverage run `npm test -- src --coverage`.
