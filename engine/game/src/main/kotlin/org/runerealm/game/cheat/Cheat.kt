package org.runerealm.game.cheat

import org.runerealm.game.entity.Player

public data class Cheat(
    public val player: Player,
    public val command: String,
    public val args: List<String>,
)
