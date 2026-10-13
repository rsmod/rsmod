package org.runerealm.api.account.saver.request

import org.runerealm.game.entity.Player

public data class AccountSaveRequest(
    val accountId: Int,
    val characterId: Int,
    val player: Player,
    val callback: AccountSaveCallback,
    var attempts: Int = 0,
)
