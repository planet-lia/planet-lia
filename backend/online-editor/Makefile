DOCKER_IMAGE=planetlia/online-editor

GOCMD=go
GOBUILD=$(GOCMD) build
GOCLEAN=$(GOCMD) clean
GOTEST=$(GOCMD) test
GOGET=$(GOCMD) get

VERSION:=$(shell cat VERSION)

BINARY_NAME=planet-lia-online-editor-manager
BINARY_MACOS=$(BINARY_NAME)_macos
BINARY_LINUX=$(BINARY_NAME)_linux_amd64

BUILD_DIR=./build
MAIN_PACKAGE="."

.PHONY: fmt
fmt:
	go fmt ./...

.PHONY: build-macos
build-macos: fmt
	cd manager && GOOS="darwin" GOARCH="amd64" CGO_ENABLED=0 $(GOBUILD) -a -installsuffix cgo -o $(BUILD_DIR)/$(BINARY_MACOS) -v $(MAIN_PACKAGE)

.PHONY: build-linux
build-linux: fmt
	cd manager && GOOS="linux" GOARCH="amd64" CGO_ENABLED=0 $(GOBUILD) -a -installsuffix cgo -o $(BUILD_DIR)/$(BINARY_LINUX) -v $(MAIN_PACKAGE)

.PHONY: test
test:
	cd manager && $(GOTEST) -v ./...

.PHONY: clean
clean:
	cd manager && $(GOCLEAN)
	rm -drf $(BUILD_DIR)

.PHONY: docker
docker:
	docker build -t $(DOCKER_IMAGE):${VERSION} .
