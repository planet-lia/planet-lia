#!/usr/bin/env bash

runTests=$1

platforms=("linux/386" "windows/386" "darwin/amd64" "linux/amd64/static")

# Cd to root of the project
pathToScript="`dirname \"$0\"`"
cd "${pathToScript}"/.. || exit

go fmt ./...

# Run tests
if [[ $runTests != "false" ]]; then
    go test ./...

    exit_status=$?
    if [[ ${exit_status} != 0 ]]; then
        echo ${exit_status}
        (>&2 echo "Running tests failed.")
        exit ${exit_status}
    fi
fi

# Try to make build dir
mkdir -p "build"

# Build for all platforms
for platform in "${platforms[@]}"
do
    echo "Building for ${platform}..."

    platformSplit=(${platform//\// })
    GOOS=${platformSplit[0]}
    GOARCH=${platformSplit[1]}
    osName=${GOOS}

    if [[ ${GOOS} == "darwin" ]]; then
        osName="macos"
    fi

    buildDir="build/lia-"${osName}

    if [[ ${#platformSplit[@]} == 3 ]]; then
       CGO_ENABLED=0
       buildDir=${buildDir}-static
    fi

    # Create buildDir for this platform
    mkdir -p ${buildDir}

    execName="lia"
    if [ "${GOOS}" = "windows" ]; then
        execName+='.exe'
    fi
    execPath="${buildDir}/${execName}"

    # Remove exec if it exists
    rm "${execPath}"

    if [[ ${CGO_ENABLED} == 0 ]]; then
        env CGO_ENABLED=0 GOOS="${GOOS}" GOARCH="${GOARCH}" go build -o ${execPath} cmd/lia/main.go
    else
        env GOOS="${GOOS}" GOARCH="${GOARCH}" go build -o ${execPath} cmd/lia/main.go
    fi
    if [ $? -ne 0 ]; then
        echo 'An error has occurred! Aborting the script execution...'
        exit $?
    fi
done


