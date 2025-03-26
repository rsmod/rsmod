package org.rsmod.api.npc

import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Npc

public val Npc.meleeStrength: Int
    get() = visType.param(params.melee_strength)

public val Npc.rangedStrength: Int
    get() = visType.param(params.ranged_strength)

public val Npc.magicStrength: Int
    get() = visType.param(params.npc_magic_damage_bonus)
