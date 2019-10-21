#!/usr/bin/env bash

# Peforms the setup of the Docker image container for the online editor.

set -e

# Download Lia-SDK
curl -s "https://api.github.com/repos/planet-lia/lia-SDK/releases/latest" | \
    jq '.assets[] | select(.name=="lia-sdk-linux.zip") | .browser_download_url' | \
    xargs -n1 wget -O lia-sdk-linux.zip
unzip lia-sdk-linux.zip
mv lia-sdk-linux/* .
wget -O ./data/game-config.json "https://files.production.cloud.planetlia.com/games/lia-1/game-config.json"
wget -O ./data/game-engine.jar "https://files.production.cloud.planetlia.com/games/lia-1/game-engine-v2.jar"

echo -n '{"analyticsallow": false, "analyticsallowedversion": "1.0.1", "trackingid": "29b36ae5-1a1c-4fa5-ac52-e64fafc6783a"}' > .lia.json
echo -n '{"analyticsallow": false, "analyticsallowedversion": "1.0.1", "trackingid": "29b36ae5-1a1c-4fa5-ac52-e64fafc6783a"}' > ~/.lia.json

#COPY ./games/lia-1/bots /home/app/games/lia-1/bots
mkdir -p /home/app/games/lia-1
cd /home/app/games/lia-1 && svn checkout https://github.com/planet-lia/planet-lia/trunk/games/lia-1/bots && cd /home/app

# Old Lia-SDK expects a lia.json file not bot.json
cp ./games/lia-1/bots/java/bot.json ./games/lia-1/bots/java/lia.json
cp ./games/lia-1/bots/kotlin/bot.json ./games/lia-1/bots/kotlin/lia.json
cp ./games/lia-1/bots/python3/bot.json ./games/lia-1/bots/python3/lia.json

mv ./games/lia-1/bots/java ./games/lia-1/bots/java_bot1
mv ./games/lia-1/bots/kotlin ./games/lia-1/bots/kotlin_bot1
mv ./games/lia-1/bots/python3 ./games/lia-1/bots/python3_bot1

cp -r ./games/lia-1/bots/java_bot1 ./games/lia-1/bots/java_bot2
cp -r ./games/lia-1/bots/kotlin_bot1 ./games/lia-1/bots/kotlin_bot2
cp -r ./games/lia-1/bots/python3_bot1 ./games/lia-1/bots/python3_bot2

ln -s /home/app/lia /usr/bin/lia
