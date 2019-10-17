DOCKER_IMAGE=planetlia/online-editor

GOCMD=go
GOBUILD=$(GOCMD) build
GOCLEAN=$(GOCMD) clean
GOTEST=$(GOCMD) test
GOGET=$(GOCMD) get

VERSION_MAJOR:=$(shell cat manager/version/version.go | awk '/versionMajor.* = / {print $$3}')
VERSION_MINOR:=$(shell cat manager/version/version.go | awk '/versionMinor.* = / {print $$3}')
VERSION_BUGFIX:=$(shell cat manager/version/version.go | awk '/versionBugfix.* = / {print $$3}')
VERSION:=$(VERSION_MAJOR).$(VERSION_MINOR).$(VERSION_BUGFIX)

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
	$(GOTEST) -v ./...

.PHONY: clean
clean:
	$(GOCLEAN)
	rm -drf $(BUILD_DIR)

.PHONY: online-editor-v1
online-editor-v1:
	docker build -t $(DOCKER_IMAGE):${VERSION} -f ./v1/Dockerfile .
