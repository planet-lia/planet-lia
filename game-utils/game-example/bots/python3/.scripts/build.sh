#!/usr/bin/env bash

if ! [[ -d "venv" ]]; then
    echo "Setting up environment. This may take some time but is only done once."
    pip3 install virtualenv
    echo "Creating Python3 virtual environment, it may take some time..."
    virtualenv -p python3 venv
fi
venv/bin/pip install -r requirements.txt
