package org.runerealm.api.combat.formulas.attributes.collector

import jakarta.inject.Inject
import java.util.EnumSet
import org.runerealm.api.combat.commons.styles.AttackStyle
import org.runerealm.api.combat.formulas.attributes.DamageReductionAttributes
import org.runerealm.api.combat.weapon.styles.AttackStyles
import org.runerealm.api.config.refs.categories
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.hat
import org.runerealm.api.player.lefthand
import org.runerealm.api.player.legs
import org.runerealm.api.player.righthand
import org.runerealm.api.player.torso
import org.runerealm.api.player.worn.EquipmentChecks
import org.runerealm.api.random.GameRandom
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.isType
import org.runerealm.game.type.obj.ObjTypeList

public class DamageReductionAttributeCollector
@Inject
constructor(private val attackStyles: AttackStyles, private val objTypes: ObjTypeList) {
    // `random` is an explicit parameter to indicate that this function relies on randomness
    // for certain effects, such as the Elysian spirit shield proc.
    public fun collectNvP(player: Player, random: GameRandom): EnumSet<DamageReductionAttributes> {
        val attributes = EnumSet.noneOf(DamageReductionAttributes::class.java)

        val shield = player.lefthand
        if (shield.isType(objs.elysian_spirit_shield) && random.of(maxExclusive = 10) < 7) {
            attributes += DamageReductionAttributes.ElysianProc
        }

        val weaponType = objTypes.getOrNull(player.righthand)
        if (weaponType != null && weaponType.isCategoryType(categories.dinhs_bulwark)) {
            val attackStyle = attackStyles.get(player)
            val passiveDelay = player.vars[varps.dinhs_passive_delay]
            val isPassiveDelayed = player.currentMapClock < passiveDelay
            if (attackStyle == AttackStyle.AggressiveMelee && !isPassiveDelayed) {
                attributes += DamageReductionAttributes.DinhsBlock
            }
        }

        if (EquipmentChecks.isJusticiarSet(player.hat, player.torso, player.legs)) {
            attributes += DamageReductionAttributes.Justiciar
        }

        return attributes
    }

    // `random` is an explicit parameter to indicate that this function relies on randomness
    // for certain effects, such as the Elysian spirit shield proc.
    public fun collectPvP(player: Player, random: GameRandom): EnumSet<DamageReductionAttributes> {
        val attributes = EnumSet.noneOf(DamageReductionAttributes::class.java)

        val shield = player.lefthand
        if (shield.isType(objs.elysian_spirit_shield) && random.of(maxExclusive = 10) < 7) {
            attributes += DamageReductionAttributes.ElysianProc
        }

        return attributes
    }
}
