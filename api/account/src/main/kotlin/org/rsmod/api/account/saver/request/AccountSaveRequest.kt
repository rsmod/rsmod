package org.rsmod.api.account.saver.request

import org.rsmod.game.entity.Player

public data class AccountSaveRequest(
    val accountId: Int,
    val characterId: Int,
    val player: Player,
    val callback: AccountSaveCallback,
    var attempts: Int = 0,
)
