package org.rsmod.api.player.stat

import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatType

public data class PlayerLevelUpEvent(
    public val player: Player,
    public val stat: StatType,
    public val oldLevel: Int,
    public val newLevel: Int,
) : UnboundEvent
