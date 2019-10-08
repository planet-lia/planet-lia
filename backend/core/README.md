# Backend
Complete backend infrastructure for Planet Lia platform.

The main backend component named `planet-lia-backend` is entirely written in [Go](https://golang.org/).

The master branch gets automatically deployed.

[Docker hub repository](https://hub.docker.com/r/planetlia/backend)

## Root Endpoints
Production: `https://api.production.cloud.planetlia.com`

GraphQL endpoint: `/graphql`

## Development
The project uses [Go Modules](https://github.com/golang/go/wiki/Modules) therefore it is not necessary to place the repository into your $GOPATH.

Just make sure you have Go Module support turned on.
You can manually activate it by running
```bash
export GO111MODULE=on
```

### Dependencies
- Go v1.13+
- Make

### Starting the Backend
```bash
cd backend

# Choose appropriate operating system
make build-macos
make build-linux

# Once you are done you can clean the build artifacts
make clean
```

You can also build the Docker image directly using
```bash
make docker
```

This will use the last commit id automatically and write it into the image.
If you wish to create a release first commit all changes then build the docker image.