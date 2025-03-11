package org.rsmod.api.npc

import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Npc

public val Npc.meleeStrength: Int
    get() = visType.param(params.melee_strength)
