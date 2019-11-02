class Response:
    def __init__(self, uid):
        self.__uid = uid
        self.commands = []

    def move_unit(self, direction):
        self.commands.append({"__type": "MoveCommand", "direction": direction})
