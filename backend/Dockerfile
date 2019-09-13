FROM golang:1.13 AS builder
ARG COMMIT_ID=""

RUN go version

COPY . /app
WORKDIR /app

# See Makefile for clarification what COMMIT_ID/COMMIT env variable does.
RUN make build-linux COMMIT=${COMMIT_ID}

FROM scratch
COPY --from=builder /app/build/planet-lia-backend_linux_amd64 .

# Default values
EXPOSE 8080

ENTRYPOINT ["/planet-lia-backend_linux_amd64"]
