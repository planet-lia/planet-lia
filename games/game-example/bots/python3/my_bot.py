import asyncio
import random

from core.bot import Bot
from core.networking_client import connect
from core.enums import Direction


# Example Python 3 bot implementation for Planet Lia game-example.
# To change this so that it can be used in some other Planet Lia game:
# 1. Change response object to fit the API format of that game
# 2. Update the basic MyBot implementation with basic bot logic
class MyBot(Bot):

    # Called only once before the match starts. It holds the
    # data that you may need before the game starts.
    def setup(self, initial_data):
        print(initial_data)
        self.initial_data = initial_data

    # Called repeatedly while the match is generating. Each
    # time you receive the current match state and can use
    # response object to issue your commands.
    def update(self, state, response):
        print(state)

        # Move in one of the four directions every update call
        r = random.randint(0, 3)
        if r == 0:
            response.move_unit(Direction.LEFT)
        elif r == 1:
            response.move_unit(Direction.RIGHT)
        elif r == 2:
            response.move_unit(Direction.UP)
        else:
            response.move_unit(Direction.DOWN)


# Connects your bot to match generator, don't change it.
if __name__ == "__main__":
    asyncio.get_event_loop().run_until_complete(connect(MyBot()))
