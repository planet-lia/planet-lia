class Response:
    def __init__(self, uid):
        self.__uid = uid
        self.commands = []

    def spawn_unit(self, planet_id, unit_type):
        self.commands.append({"__type": "SpawnUnitCommand",
                              "planetId": planet_id,
                              "type": unit_type})

    def send_unit(self, unit_id, destination_planet_id):
        self.commands.append({"__type": "SendUnitCommand",
                              "unitId": unit_id,
                              "destinationPlanetId": destination_planet_id})
