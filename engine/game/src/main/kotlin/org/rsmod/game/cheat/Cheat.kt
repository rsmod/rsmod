package org.rsmod.game.cheat

import org.rsmod.game.entity.Player

public data class Cheat(
    public val player: Player,
    public val command: String,
    public val args: List<String>,
)
