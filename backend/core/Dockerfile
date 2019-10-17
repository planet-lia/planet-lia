FROM golang:1.13 AS builder
ARG COMMIT_ID=""

# CA-certificates is required to call HTTPS endpoints.
RUN apt-get update && apt-get -y install ca-certificates

COPY . /app
WORKDIR /app

# See Makefile for clarification what COMMIT_ID/COMMIT env variable does.
RUN make build-linux COMMIT=${COMMIT_ID}


FROM scratch

COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=builder /app/build/planet-lia-backend-core_linux_amd64 .

# Default values
EXPOSE 8080

ENTRYPOINT ["/planet-lia-backend-core_linux_amd64"]
