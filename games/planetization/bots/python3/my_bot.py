import asyncio
import random

from core.bot import Bot
from core.networking_client import connect
from core.enums import *


# Example Python3 bot implementation for Planetization game.
class MyBot(Bot):

    # Called only once before the match starts. It holds the
    # data that you may need to setup your bot.
    def setup(self, initial_data):
        self.data = initial_data

    # Called repeatedly while the match is generating. Read
    # game state using state parameter and issue your
    # commands with response parameter.
    def update(self, state, response):

        # Iterate through all the planets you own
        for planet in state["yourPlanets"]:

            # If the planet has enough resources, spawn new unit on it
            if planet["canSpawnNewUnit"]:
                unit_type = UnitType.WORKER if (random.random() < 0.5) else UnitType.WARRIOR
                response.spawn_unit(planet["id"], unit_type)

            max_active_workers = self.data["maxActiveWorkersPerPlanet"]
            num_bots_on_planet = len(planet["idsOfUnitsOnPlanet"])

            # Only a certain number of workers can mine resources on
            # a planet. Let's send the rest of them somewhere else.
            for i in range(max_active_workers, num_bots_on_planet):
                unit_id = planet["idsOfUnitsOnPlanet"][i]

                # Randomly select if you will send the unit to a free or opponent planet
                planets = state["freePlanets"] if random.random() < 0.5 else state["opponentPlanets"]

                if len(planets) > 0:
                    # Select a random planet in planets and send your unit there
                    destination_planet_index = random.randint(0, len(planets) - 1)
                    destination_planet_id = planets[destination_planet_index]["id"]
                    response.send_unit(unit_id, destination_planet_id)


# Connects your bot to match generator, don't change it.
if __name__ == "__main__":
    asyncio.get_event_loop().run_until_complete(connect(MyBot()))
