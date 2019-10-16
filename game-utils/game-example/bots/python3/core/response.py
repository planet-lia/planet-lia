
class Response:
    def __init__(self, uid):
        self.__uid = uid
        self.commands = []

    def add_command(self, command):
        self.commands.append(command)

