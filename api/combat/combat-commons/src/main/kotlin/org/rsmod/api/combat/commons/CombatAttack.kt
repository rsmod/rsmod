package org.rsmod.api.combat.commons

import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.game.obj.InvObj

public sealed class CombatAttack {
    public data class Melee(
        val weapon: InvObj?,
        val type: MeleeAttackType?,
        val style: MeleeAttackStyle?,
        val stance: CombatStance,
    ) : CombatAttack()

    public data class Ranged(
        val weapon: InvObj,
        val type: RangedAttackType?,
        val style: RangedAttackStyle?,
    ) : CombatAttack()

    public sealed class Magic : CombatAttack()

    public data class Spell(val weapon: InvObj?, val spell: MagicSpell, val defensive: Boolean) :
        Magic()

    public data class Staff(val weapon: InvObj, val defensive: Boolean) : Magic()
}
