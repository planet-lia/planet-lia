#!/usr/bin/env bash

pathToScript="`dirname \"$0\"`"
cd "${pathToScript}/.." || exit

pathToBuild="scripts/serveReleasesBuild"
pathToGameExampleMatchGenerator="../games/game-example/match-generator"
pathToGameExampleAssets="../games/game-example/assets"
pathToGameExampleBots="../games/game-example/bots"

rm -r ${pathToBuild}

# Build example-game match-generator
"./${pathToGameExampleMatchGenerator}/gradlew" -p "${pathToGameExampleMatchGenerator}/" dist

# Zip match-generator.jar and assets together
mkdir -p "${pathToBuild}/game-example"
cp "${pathToGameExampleMatchGenerator}/desktop/build/libs/match-generator.jar" "${pathToBuild}/game-example/match-generator.jar"
cp -r "${pathToGameExampleAssets}" "${pathToBuild}/game-example/assets"
(
  cd "${pathToBuild}" || exit
  zip -r "game-example.zip" "game-example" || exit
  rm -r game-example
)

# Zip example-game bots
cp -r "${pathToGameExampleBots}" "${pathToBuild}/bots"
rm -r "${pathToBuild}/bots/python3/env"
(
  cd "${pathToBuild}/bots" || exit
  zip -r "java.zip" "java" || exit
  zip -r "kotlin.zip" "kotlin" || exit
  zip -r "python3.zip" "python3" || exit
)

# Serve all
cd "scripts" || exit
python3 -m http.server 4447