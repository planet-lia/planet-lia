package core

class MatchDetails(
        /**
         * Index of the bot in botsDetails to which
         * this instance of MatchDetails was sent to
         */
        var yourBotIndex: Int,

        /**
         * List of all bots that participate in this
         * match with their details
         */
        var botsDetails: Array<BotDetails>
)
