FROM golang:1.13 AS builder

RUN go version

COPY . /app
WORKDIR /app

RUN make build-linux

FROM ubuntu:18.04

RUN useradd -ms /bin/bash app
WORKDIR /home/app

COPY . /home/app

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk=8u222-b10-1ubuntu1~18.04.1 \
    python3=3.6.7-1~18.04 python3-virtualenv virtualenv python3-pip \
    curl jq wget unzip subversion && \
    apt-get clean && \
    apt-get autoclean

RUN bash ./setup.sh
RUN bash ./prebuild.sh

RUN chown app: -R ./*
USER app

COPY --from=builder /app/manager/build/planet-lia-online-editor-manager_linux_amd64 .

ENTRYPOINT ["./planet-lia-online-editor-manager_linux_amd64"]