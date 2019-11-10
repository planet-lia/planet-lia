#!/usr/bin/env bash

pathToScript="`dirname \"$0\"`"
cd "${pathToScript}/.." || exit

gameName="$1"

pathToBuild="scripts/serveReleasesBuild"
pathToGameExampleMatchGenerator="../games/${gameName}/match-generator"
pathToGameExampleAssets="../games/${gameName}/assets"
pathToGameExampleBots="../games/${gameName}/bots"

rm -r ${pathToBuild}

# Build example-game match-generator
"./${pathToGameExampleMatchGenerator}/gradlew" -p "${pathToGameExampleMatchGenerator}/" dist

# Zip match-generator.jar and assets together
mkdir -p "${pathToBuild}/${gameName}"
cp "${pathToGameExampleMatchGenerator}/desktop/build/libs/match-generator.jar" "${pathToBuild}/${gameName}/match-generator.jar"
cp -r "${pathToGameExampleAssets}" "${pathToBuild}/${gameName}/assets"
(
  cd "${pathToBuild}" || exit
  zip -r "${gameName}.zip" "${gameName}" || exit
  rm -r ${gameName}
)

# Zip example-game bots
cp -r "${pathToGameExampleBots}" "${pathToBuild}/bots"
rm -r "${pathToBuild}/bots/python3/env"
rm -r "${pathToBuild}/bots/python3/venv"
(
  cd "${pathToBuild}/bots" || exit
  zip -r "java.zip" "java" || exit
  zip -r "kotlin.zip" "kotlin" || exit
  zip -r "python3.zip" "python3" || exit
)

# Serve all
cd "scripts" || exit
python3 -m http.server 4447