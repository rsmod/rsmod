package org.rsmod.api.combat.formulas.maxhit.melee

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.EquipmentChecks
import org.rsmod.api.combat.formulas.attributes.DamageReductionAttributes
import org.rsmod.api.combat.maxhit.npc.NpcMeleeMaxHit
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.config.refs.objs
import org.rsmod.api.npc.meleeStrength
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isAnyType
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.Wearpos

public class NvPMeleeMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val attackStyles: AttackStyles,
) {
    public fun getMaxHit(npc: Npc, target: Player, attackType: MeleeAttackType?): Int {
        return computeMaxHit(npc, target, attackType)
    }

    public fun computeMaxHit(npc: Npc, target: Player, attackType: MeleeAttackType?): Int {
        val effectiveStrength = NpcMeleeMaxHit.calculateEffectiveStrength(npc.strengthLvl)
        val baseDamage = NpcMeleeMaxHit.calculateBaseDamage(effectiveStrength, npc.meleeStrength)

        val defenceBonus = target.getDefenceBonus(attackType)
        val reductionAttributes = collectReductionAttributes(target)
        return MeleeMaxHitOperations.applyDamageReductions(
            baseDamage,
            defenceBonus,
            reductionAttributes,
        )
    }

    private fun Player.getDefenceBonus(attackType: MeleeAttackType?): Int =
        when (attackType) {
            MeleeAttackType.Stab -> bonuses.defensiveStabBonus(this)
            MeleeAttackType.Slash -> bonuses.defensiveSlashBonus(this)
            MeleeAttackType.Crush -> bonuses.defensiveCrushBonus(this)
            null -> 0
        }

    private fun collectReductionAttributes(player: Player): EnumSet<DamageReductionAttributes> {
        val reductionAttributes = EnumSet.noneOf(DamageReductionAttributes::class.java)

        val shield = player.worn[Wearpos.LeftHand.slot]
        if (shield.isType(objs.elysian_spirit_shield) && random.of(maxExclusive = 10) < 7) {
            reductionAttributes += DamageReductionAttributes.ElysianProc
        }

        if (shield.isAnyType(objs.dinhs_bulwark, objs.dinhs_blazing_bulwark)) {
            val attackStyle = attackStyles.get(player)
            // Dinh's bulwark "Block" style is considered a "None" attack style, aka `null`.
            if (attackStyle == null) {
                reductionAttributes += DamageReductionAttributes.DinhsBlock
            }
        }

        val helm = player.worn[Wearpos.Hat.slot]
        val top = player.worn[Wearpos.Torso.slot]
        val legs = player.worn[Wearpos.Legs.slot]
        if (EquipmentChecks.isJusticiarSet(helm, top, legs)) {
            reductionAttributes += DamageReductionAttributes.Justiciar
        }

        return reductionAttributes
    }
}
