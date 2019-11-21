package core.api


interface Command

class Response {
    var __uid: Int = 0
    private val commands = ArrayList<Command>()

    fun moveUnit(direction: Direction) {
        commands.add(MoveCommand(direction))
    }
}

enum class Direction {
    LEFT, RIGHT, UP, DOWN
}

class MoveCommand(var direction: Direction) : Command {
    var __type = this.javaClass.simpleName
}