GOCMD=go
GOBUILD=$(GOCMD) build
GOCLEAN=$(GOCMD) clean
GOTEST=$(GOCMD) test
GOGET=$(GOCMD) get

BUILD_DATE:=$(shell date -u +"%Y-%m-%dT%H:%M:%SZ")
VERSION_MAJOR:=$(shell cat version/version.go | awk '/versionMajor = / {print $$3}')
VERSION_MINOR:=$(shell cat version/version.go | awk '/versionMinor = / {print $$3}')
COMMIT:=$(shell git rev-parse HEAD)
VERSION:=$(VERSION_MAJOR).$(VERSION_MINOR)-$(COMMIT)
IMPORT_PATH=github.com/planet-lia/planet-lia/backend
LFLAGS=-ldflags "-X ${IMPORT_PATH}/version.buildDate=${BUILD_DATE} -X ${IMPORT_PATH}/version.commit=${COMMIT}"

BINARY_NAME=planet-lia-backend
BINARY_MACOS=$(BINARY_NAME)_macos
BINARY_LINUX=$(BINARY_NAME)_linux_amd64

BUILD_DIR=./build
MAIN_PACKAGE="."

DOCKER_IMAGE=planetlia/backend

.PHONY: fmt
fmt:
	go fmt ./...

.PHONY: build-macos
build-macos: fmt
	GOOS="darwin" GOARCH="amd64" CGO_ENABLED=0 $(GOBUILD) -a -installsuffix cgo -o $(BUILD_DIR)/$(BINARY_MACOS) $(LFLAGS) -v $(MAIN_PACKAGE)

.PHONY: build-linux
build-linux: fmt
	GOOS="linux" GOARCH="amd64" CGO_ENABLED=0 $(GOBUILD) -a -installsuffix cgo -o $(BUILD_DIR)/$(BINARY_LINUX) $(LFLAGS) -v $(MAIN_PACKAGE)

.PHONY: test
test:
	$(GOTEST) -v ./...

.PHONY: clean
clean:
	$(GOCLEAN)
	rm -drf $(BUILD_DIR)

.PHONY: docker
docker:
# We inject the COMMIT_ID here into docker because during the building of the Docker image we lose \
# our .git directory since it's in the parent directory of backend. We could move the Dockerfile into \
# the same directory where .git is, but that would be messy. \

# As a work arround we get the commit id before launching make inside the Docker builder image.
# We set the COMMIT_ID build argument which overrides the COMMIT env variable in the builder image when
# calling make.
	docker build -t $(DOCKER_IMAGE):$(VERSION) -t $(DOCKER_IMAGE):latest --build-arg COMMIT_ID=${COMMIT} .
