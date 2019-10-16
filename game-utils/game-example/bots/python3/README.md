# Python 3 Default Bot

Starter Python 3 bot implementation.

## Setup and Run Bot Manually
### Windows
```bash
# Setup 
python -m pip install virtualenv
python3 -m venv env
.\venv\Scripts\pip install -r requirements.txt
# Run
.\venv\Scripts\python my_bot.py
```

### Linux / macOS
```bash
# Setup 
python -m pip install virtualenv
.\venv\Scripts\pip install -r requirements.txt
python -m venv venv
# Run
venv/bin/python3 my_bot.py -p "$1" -i "$2"
```

### Use PyCharm
Open bot in PyCharm:
* Go to `Settings` > `Project: <name-of-bot>` > `Project Interpreter` > `Project Interpreter` (settings button top right) > `Add local` > 
Choose `New environment` and set it to Python 3 > `Ok` 
* Open new termianl inside PyCharm and run `pip install -r requirements.txt`
* Right click on `my_bot.py` and click `Run 'my_bot'`