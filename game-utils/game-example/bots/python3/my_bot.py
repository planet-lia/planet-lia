import asyncio

from core.bot import Bot
from core.networking_client import connect


# The default Python 3 bot implementation that is not specific
# to any Planet Lia game but instead serves as a base for
# all game specific bot implementations.
class MyBot(Bot):

    # Called only once before the match starts. It holds the
    # data that you may need before the game starts.
    def setup(self, initial_data):
        # TODO
        # This is the default bot that is not configured
        # for any specific game.
        # See how to support a specific game:
        # - <TODO_LINK>
        pass

    # Called repeatedly while the match is generating. Each
    # time you receive the current match state and can use
    # response object to issue your commands.
    def update(self, state, response):
        # TODO
        # This is the default bot that is not configured
        # for any specific game.
        # See how to support a specific game:
        # - <TODO_LINK>
        pass


# Connects your bot to match generator, don't change it.
if __name__ == "__main__":
    asyncio.get_event_loop().run_until_complete(connect(MyBot()))
