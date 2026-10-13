package org.runerealm.api.npc

import org.runerealm.api.config.refs.params
import org.runerealm.game.entity.Npc

public val Npc.meleeStrength: Int
    get() = visType.param(params.melee_strength)

public val Npc.rangedStrength: Int
    get() = visType.param(params.ranged_strength)

public val Npc.magicStrength: Int
    get() = visType.param(params.npc_magic_damage_bonus)
