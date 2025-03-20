package org.rsmod.api.combat.formulas.attributes.collector

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.formulas.attributes.DamageReductionAttributes
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.hat
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.legs
import org.rsmod.api.player.righthand
import org.rsmod.api.player.torso
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.ObjTypeList

public class DamageReductionAttributeCollector
@Inject
constructor(private val attackStyles: AttackStyles, private val objTypes: ObjTypeList) {
    // `random` is an explicit parameter to indicate that this function relies on randomness
    // for certain effects, such as the Elysian spirit shield proc.
    public fun collect(
        player: Player,
        pvp: Boolean,
        random: GameRandom,
    ): EnumSet<DamageReductionAttributes> {
        val attributes = EnumSet.noneOf(DamageReductionAttributes::class.java)

        val shield = player.lefthand
        if (shield.isType(objs.elysian_spirit_shield) && random.of(maxExclusive = 10) < 7) {
            attributes += DamageReductionAttributes.ElysianProc
        }

        if (!pvp) {
            val weaponType = player.righthand?.let(objTypes::get)

            if (weaponType != null && weaponType.isCategoryType(categories.dinhs_bulwark)) {
                val attackStyle = attackStyles.get(player)
                val passiveDelay = player.vars[varps.dinhs_passive_delay]
                val isPassiveDelayed = player.currentMapClock < passiveDelay
                if (attackStyle == AttackStyle.DefensiveMelee && !isPassiveDelayed) {
                    attributes += DamageReductionAttributes.DinhsBlock
                }
            }

            if (EquipmentChecks.isJusticiarSet(player.hat, player.torso, player.legs)) {
                attributes += DamageReductionAttributes.Justiciar
            }
        }

        return attributes
    }
}
