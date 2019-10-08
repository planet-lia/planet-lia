
class Response:
    def __init__(self, uid):
        self.__uid = uid
        self.commands = []

    def set_speed(self, unit_id, speed):
        self.commands.append({"__type": "SpeedEvent", "unitId": unit_id, "speed": speed})

    def set_rotation(self, unit_id, rotation):
        self.commands.append({"__type": "RotationEvent", "unitId": unit_id, "rotation": rotation})

    def shoot(self, unit_id):
        self.commands.append({"__type": "ShootEvent", "unitId": unit_id})

    def navigation_start(self, unit_id, x, y, move_backwards=False):
        self.commands.append({"__type": "NavigationStartEvent", "unitId": unit_id,
                                           "x": x, "y": y, "moveBackwards": move_backwards})

    def navigation_stop(self, unit_id):
        self.commands.append({"__type": "NavigationStopEvent", "unitId": unit_id})

    def say_something(self, unit_id, text):
        self.commands.append({"__type": "SaySomethingEvent", "unitId": unit_id, "text": text})

    def spawn_unit(self, unit_type):
        self.commands.append({"__type": "SpawnUnitEvent", "type": unit_type})
