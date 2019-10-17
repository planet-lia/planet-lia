#!/usr/bin/env bash

set -e

# Pre-build bots for all langauges so that it will be faster for subseqent matches
lia compile /home/app/games/lia-1/bots/java_bot1
lia compile /home/app/games/lia-1/bots/java_bot2

lia compile /home/app/games/lia-1/bots/kotlin_bot1
lia compile /home/app/games/lia-1/bots/kotlin_bot2

lia compile /home/app/games/lia-1/bots/python3_bot1
lia compile /home/app/games/lia-1/bots/python3_bot2

# Change the `-i` flag into the `-t` flag - old Lia-SDK limitation
sed "s/-i/-t/" /home/app/data/languages/java_Run.sh > /home/app/data/languages/java_Run.sh.bak && mv /home/app/data/languages/java_Run.sh.bak /home/app/data/languages/java_Run.sh
sed "s/-i/-t/" /home/app/data/languages/python3_Run.sh > /home/app/data/languages/python3_Run.sh.bak && mv /home/app/data/languages/python3_Run.sh.bak /home/app/data/languages/python3_Run.sh

sed "s/-i/-t/" /home/app/games/lia-1/bots/java_bot1/run.sh > /home/app/games/lia-1/bots/java_bot1/run.sh.bak && mv /home/app/games/lia-1/bots/java_bot1/run.sh.bak /home/app/games/lia-1/bots/java_bot1/run.sh
sed "s/-i/-t/" /home/app/games/lia-1/bots/java_bot2/run.sh > /home/app/games/lia-1/bots/java_bot2/run.sh.bak && mv /home/app/games/lia-1/bots/java_bot2/run.sh.bak /home/app/games/lia-1/bots/java_bot2/run.sh

sed "s/-i/-t/" /home/app/games/lia-1/bots/kotlin_bot1/run.sh > /home/app/games/lia-1/bots/kotlin_bot1/run.sh.bak && mv /home/app/games/lia-1/bots/kotlin_bot1/run.sh.bak /home/app/games/lia-1/bots/kotlin_bot1/run.sh
sed "s/-i/-t/" /home/app/games/lia-1/bots/kotlin_bot2/run.sh > /home/app/games/lia-1/bots/kotlin_bot2/run.sh.bak && mv /home/app/games/lia-1/bots/kotlin_bot2/run.sh.bak /home/app/games/lia-1/bots/kotlin_bot2/run.sh

sed "s/-i/-t/" /home/app/games/lia-1/bots/python3_bot1/run.sh > /home/app/games/lia-1/bots/python3_bot1/run.sh.bak && mv /home/app/games/lia-1/bots/python3_bot1/run.sh.bak /home/app/games/lia-1/bots/python3_bot1/run.sh
sed "s/-i/-t/" /home/app/games/lia-1/bots/python3_bot2/run.sh > /home/app/games/lia-1/bots/python3_bot2/run.sh.bak && mv /home/app/games/lia-1/bots/python3_bot2/run.sh.bak /home/app/games/lia-1/bots/python3_bot2/run.sh

