package org.rsmod.api.combat.commons.magic

import org.rsmod.game.type.obj.ObjType

public data class MagicSpell(
    public val obj: ObjType,
    public val spellbook: Spellbook,
    public val maxHit: Int,
)
