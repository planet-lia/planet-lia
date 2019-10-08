package core

import core.api.InitialData
import core.api.MatchState
import core.api.Response

interface Bot {
    fun setup(data: InitialData)
    fun update(state: MatchState, response: Response)
}
