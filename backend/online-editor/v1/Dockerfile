FROM golang:1.13 AS builder

RUN go version

COPY ./backend/online-editor /app
WORKDIR /app

RUN make build-linux

FROM ubuntu:18.04

RUN useradd -ms /bin/bash app
WORKDIR /home/app

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk=8u222-b10-1ubuntu1~18.04.1 \
    python3=3.6.7-1~18.04 python3-virtualenv virtualenv python3-pip \
    curl jq wget unzip

# Download Lia-SDK
RUN curl -s "https://api.github.com/repos/planet-lia/lia-SDK/releases/latest" | \
    jq '.assets[] | select(.name=="lia-sdk-linux.zip") | .browser_download_url' | \
    xargs -n1 wget -O lia-sdk-linux.zip
RUN unzip lia-sdk-linux.zip
RUN mv lia-sdk-linux/* .
RUN wget -O ./data/game-config.json "https://files.production.cloud.planetlia.com/games/lia-1/game-config.json"
RUN wget -O ./data/game-engine.jar "https://files.production.cloud.planetlia.com/games/lia-1/game-engine.jar"

RUN echo -n '{"analyticsallow": false, "analyticsallowedversion": "1.0.1", "trackingid": "29b36ae5-1a1c-4fa5-ac52-e64fafc6783a"}' > .lia.json
RUN echo -n '{"analyticsallow": false, "analyticsallowedversion": "1.0.1", "trackingid": "29b36ae5-1a1c-4fa5-ac52-e64fafc6783a"}' > ~/.lia.json

COPY ./games/lia-1/bots /home/app/games/lia-1/bots

# Old Lia-SDK expects a lia.json file not bot.json
RUN cp ./games/lia-1/bots/java/bot.json ./games/lia-1/bots/java/lia.json
RUN cp ./games/lia-1/bots/kotlin/bot.json ./games/lia-1/bots/kotlin/lia.json
RUN cp ./games/lia-1/bots/python3/bot.json ./games/lia-1/bots/python3/lia.json

RUN mv ./games/lia-1/bots/java ./games/lia-1/bots/java_bot1
RUN mv ./games/lia-1/bots/kotlin ./games/lia-1/bots/kotlin_bot1
RUN mv ./games/lia-1/bots/python3 ./games/lia-1/bots/python3_bot1

RUN cp -r ./games/lia-1/bots/java_bot1 ./games/lia-1/bots/java_bot2
RUN cp -r ./games/lia-1/bots/kotlin_bot1 ./games/lia-1/bots/kotlin_bot2
RUN cp -r ./games/lia-1/bots/python3_bot1 ./games/lia-1/bots/python3_bot2

RUN ln -s /home/app/lia /usr/bin/lia

RUN chown app: -R ./*
USER app

# Pre-build bots for all langauges so that it will be faster for subseqent matches
RUN lia compile /home/app/games/lia-1/bots/java_bot1
RUN lia compile /home/app/games/lia-1/bots/java_bot2

RUN lia compile /home/app/games/lia-1/bots/kotlin_bot1
RUN lia compile /home/app/games/lia-1/bots/kotlin_bot2

RUN lia compile /home/app/games/lia-1/bots/python3_bot1
RUN lia compile /home/app/games/lia-1/bots/python3_bot2

# Change the `-i` flag into the `-t` flag - old Lia-SDK limitation
RUN sed "s/-i/-t/" /home/app/data/languages/java_Run.sh > /home/app/data/languages/java_Run.sh.bak && mv /home/app/data/languages/java_Run.sh.bak /home/app/data/languages/java_Run.sh
RUN sed "s/-i/-t/" /home/app/data/languages/python3_Run.sh > /home/app/data/languages/python3_Run.sh.bak && mv /home/app/data/languages/python3_Run.sh.bak /home/app/data/languages/python3_Run.sh

RUN sed "s/-i/-t/" /home/app/games/lia-1/bots/java_bot1/run.sh > /home/app/games/lia-1/bots/java_bot1/run.sh.bak && mv /home/app/games/lia-1/bots/java_bot1/run.sh.bak /home/app/games/lia-1/bots/java_bot1/run.sh
RUN sed "s/-i/-t/" /home/app/games/lia-1/bots/java_bot2/run.sh > /home/app/games/lia-1/bots/java_bot2/run.sh.bak && mv /home/app/games/lia-1/bots/java_bot2/run.sh.bak /home/app/games/lia-1/bots/java_bot2/run.sh

RUN sed "s/-i/-t/" /home/app/games/lia-1/bots/kotlin_bot1/run.sh > /home/app/games/lia-1/bots/kotlin_bot1/run.sh.bak && mv /home/app/games/lia-1/bots/kotlin_bot1/run.sh.bak /home/app/games/lia-1/bots/kotlin_bot1/run.sh
RUN sed "s/-i/-t/" /home/app/games/lia-1/bots/kotlin_bot2/run.sh > /home/app/games/lia-1/bots/kotlin_bot2/run.sh.bak && mv /home/app/games/lia-1/bots/kotlin_bot2/run.sh.bak /home/app/games/lia-1/bots/kotlin_bot2/run.sh

RUN sed "s/-i/-t/" /home/app/games/lia-1/bots/python3_bot1/run.sh > /home/app/games/lia-1/bots/python3_bot1/run.sh.bak && mv /home/app/games/lia-1/bots/python3_bot1/run.sh.bak /home/app/games/lia-1/bots/python3_bot1/run.sh
RUN sed "s/-i/-t/" /home/app/games/lia-1/bots/python3_bot2/run.sh > /home/app/games/lia-1/bots/python3_bot2/run.sh.bak && mv /home/app/games/lia-1/bots/python3_bot2/run.sh.bak /home/app/games/lia-1/bots/python3_bot2/run.sh


COPY --from=builder /app/build/planet-lia-online-editor-manager_linux_amd64 .

ENTRYPOINT ["./planet-lia-online-editor-manager_linux_amd64"]