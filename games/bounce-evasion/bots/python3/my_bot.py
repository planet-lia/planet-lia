import asyncio
import random

from core.bot import Bot
from core.networking_client import connect
from core.enums import Direction


# Example Python 3 bot implementation for Planet Lia Bounce Evasion.
class MyBot(Bot):

    # Called only once before the match starts. It holds the
    # data that you may need before the game starts.
    def setup(self, initial_data):
        print(initial_data)
        self.initial_data = initial_data

        # Print out the map
        for y in range(initial_data["mapHeight"] - 1, -1, -1):
            for x in range(0, initial_data["mapWidth"]):
                print("_" if initial_data["map"][y][x] else "#", end='')
            print()

    # Called repeatedly while the match is generating. Each
    # time you receive the current match state and can use
    # response object to issue your commands.
    def update(self, state, response):
        # Find and send your unit to a random direction that
        # moves it to a valid field on the map
        # TODO: Remove this code and implement a proper path finding!
        while True:
            r = random.randint(0, 3)

            # Pick a random direction to move
            if r == 0:
                direction = Direction.LEFT
            elif r == 1:
                direction = Direction.RIGHT
            elif r == 2:
                direction = Direction.UP
            else:
                direction = Direction.DOWN

            # Find on which position this move will send your unit
            new_x = state["yourUnit"]["x"]
            new_y = state["yourUnit"]["y"]
            if direction == Direction.LEFT:
                new_x -= 1
            elif direction == Direction.RIGHT:
                new_x += 1
            if direction == Direction.UP:
                new_y += 1
            elif direction == Direction.DOWN:
                new_y -= 1

            # If the new position is on the map then send the unit towards
            # that direction and break the loop, else try again
            map_width = self.initial_data["mapWidth"]
            map_height = self.initial_data["mapHeight"]
            if new_x >= 0 and new_y >= 0 and new_x < map_width and new_y < map_height \
                    and self.initial_data["map"][new_y][new_x]:
                response.move_unit(direction)
                break

# Connects your bot to match generator, don't change it.
if __name__ == "__main__":
    asyncio.get_event_loop().run_until_complete(connect(MyBot()))
