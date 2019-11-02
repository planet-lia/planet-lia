@echo off

IF exist "venv" (
    echo virtualenv exists in venv
) ELSE (
    python -m pip install virtualenv
    echo Creating Python3 virtual environment, it may take some time...
    python -m venv venv
)
.\venv\Scripts\pip install -r requirements.txt
