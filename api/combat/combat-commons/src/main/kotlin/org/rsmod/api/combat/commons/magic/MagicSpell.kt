package org.rsmod.api.combat.commons.magic

import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.obj.ObjType

public data class MagicSpell(
    public val obj: ObjType,
    public val name: String,
    public val component: ComponentType,
    public val spellbook: Spellbook?,
    public val type: MagicSpellType,
    public val maxHit: Int,
    public val levelReq: Int,
    public val castXp: Double,
    public val objReqs: List<ObjRequirement>,
) {
    public data class ObjRequirement(val obj: ObjType, val count: Int, val wornSlot: Int?)
}
