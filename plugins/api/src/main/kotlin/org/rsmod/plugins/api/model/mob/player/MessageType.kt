package org.rsmod.plugins.api.model.mob.player

object MessageType {

    const val GAME_MESSAGE = 0
    const val MOD_CHAT = 1
    const val PUBLIC_CHAT = 2
    const val PRIVATE_CHAT = 3
    const val ENGINE = 4
    const val LOGIN_LOGOUT_NOTIFICATION = 5
    const val PRIVATE_CHAT_OUT = 6
    const val MOD_PRIVATE_CHAT = 7
    const val FRIENDS_CHAT = 9
    const val FRIENDS_CHAT_NOTIFICATION = 11
    const val BROADCAST = 14
    const val SNAPSHOT_FEEDBACK = 26
    const val ITEM_EXAMINE = 27
    const val NPC_EXAMINE = 28
    const val OBJECT_EXAMINE = 29
    const val FRIEND_NOTIFICATION = 30
    const val IGNORE_NOTIFICATION = 31
    const val AUTO_TYPER = 90
    const val MOD_AUTO_TYPER = 91
    const val CONSOLE = 99
    const val TRADE_REQ = 101
    const val TRADE = 102
    const val CHALREQ_TRADE = 103
    const val CHALREQ_FRIENDS_CHAT = 104
    const val SPAM = 105
    const val PLAYER_RELATED = 106
    const val TEN_SEC_TIMEOUT = 107
    const val FILTERED = 109
}
