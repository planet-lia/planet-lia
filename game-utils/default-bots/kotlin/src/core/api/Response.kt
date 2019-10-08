package core.api

import java.util.ArrayList

class Response {
    var __uid: Int = 0
    private val commands = ArrayList<Any>()

    fun addCommand(command: Any) {
        commands.add(command)
    }
}
