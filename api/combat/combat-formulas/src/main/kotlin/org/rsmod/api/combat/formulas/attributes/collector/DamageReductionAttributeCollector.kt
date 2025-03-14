package org.rsmod.api.combat.formulas.attributes.collector

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.formulas.EquipmentChecks
import org.rsmod.api.combat.formulas.attributes.DamageReductionAttributes
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.config.refs.objs
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isAnyType
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.Wearpos

public class DamageReductionAttributeCollector
@Inject
constructor(private val attackStyles: AttackStyles) {
    // `random` is an explicit parameter to indicate that this function relies on randomness
    // for certain effects, such as the Elysian spirit shield proc.
    public fun collect(player: Player, random: GameRandom): EnumSet<DamageReductionAttributes> {
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
