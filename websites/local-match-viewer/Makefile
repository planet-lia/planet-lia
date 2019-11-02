.PHONY: install
install:
	npm install

.PHONY: setup
setup:
	# Remove old _dependencies
	rm -rf src/_dependencies
	# Copy new match_viewer to _dependencies
	mkdir src/_dependencies
	cp -r ../../game-utils/match-viewer/src/match-viewer src/_dependencies/match-viewer
	cp -r ../../game-utils/match-viewer/src/components src/_dependencies/components

.PHONY: build
build:
	make setup
	npm run-script build

.PHONY: dev
dev:
	make setup
	REACT_APP_ASSETS_SERVER_PORT=4444 npm run-script start
