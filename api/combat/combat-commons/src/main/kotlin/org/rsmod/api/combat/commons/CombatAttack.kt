package org.rsmod.api.combat.commons

import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.game.obj.InvObj

public sealed class CombatAttack {
    public sealed class PlayerAttack : CombatAttack()

    public data class Melee(
        val weapon: InvObj?,
        val type: MeleeAttackType?,
        val style: MeleeAttackStyle?,
        val stance: CombatStance,
    ) : PlayerAttack()

    public data class Ranged(
        val weapon: InvObj,
        val type: RangedAttackType?,
        val style: RangedAttackStyle?,
    ) : PlayerAttack()

    public sealed class Magic : PlayerAttack()

    public data class Spell(val weapon: InvObj?, val spell: MagicSpell, val defensive: Boolean) :
        Magic()

    public data class Staff(val weapon: InvObj, val defensive: Boolean) : Magic()

    public sealed class NpcAttack : CombatAttack()

    public data class NpcMelee(val type: MeleeAttackType?) : NpcAttack()

    public data class NpcRanged(val type: RangedAttackType) : NpcAttack()

    public data class NpcMagic(val maxHit: Int) : NpcAttack()
}
