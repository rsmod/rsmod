package org.rsmod.api.combat.formulas.attributes.collector

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.attributes.CombatRangedAttributes
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.front
import org.rsmod.api.player.hat
import org.rsmod.api.player.legs
import org.rsmod.api.player.righthand
import org.rsmod.api.player.torso
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.isAnyType
import org.rsmod.game.inv.isType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

public class CombatRangedAttributeCollector @Inject constructor(private val objTypes: ObjTypeList) {
    public fun collect(
        player: Player,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
    ): EnumSet<CombatRangedAttributes> {
        val attributes = EnumSet.noneOf(CombatRangedAttributes::class.java)

        if (attackType == RangedAttackType.Heavy) {
            attributes += CombatRangedAttributes.Heavy
        }

        val weapon = player.righthand
        val weaponType = objTypes.getOrNull(weapon)
        if (weaponType != null && weaponType.isCategoryType(categories.chinchompa)) {
            val chinchompaFuse =
                when (attackStyle) {
                    RangedAttackStyle.Accurate -> CombatRangedAttributes.ShortFuse
                    RangedAttackStyle.Rapid -> CombatRangedAttributes.MediumFuse
                    RangedAttackStyle.Longrange -> CombatRangedAttributes.LongFuse
                    null -> null
                }

            if (chinchompaFuse != null) {
                attributes += chinchompaFuse
            }
        }

        if (EquipmentChecks.isCrystalBow(weapon)) {
            attributes += CombatRangedAttributes.CrystalBow
        }

        val helm = player.hat
        if (EquipmentChecks.isCrystalHelm(helm)) {
            attributes += CombatRangedAttributes.CrystalHelm
        }

        val body = player.torso
        if (EquipmentChecks.isCrystalBody(body)) {
            attributes += CombatRangedAttributes.CrystalBody
        }

        val legs = player.legs
        if (EquipmentChecks.isCrystalLegs(legs)) {
            attributes += CombatRangedAttributes.CrystalLegs
        }

        if (player.skullIcon == constants.skullicon_forinthry_surge) {
            attributes += CombatRangedAttributes.ForinthrySurge
        }

        val amulet = player.front
        if (amulet.isType(objs.amulet_of_avarice)) {
            attributes += CombatRangedAttributes.AmuletOfAvarice
        } else if (amulet.isType(objs.salve_amulet_ei)) {
            attributes += CombatRangedAttributes.SalveAmuletEi
        } else if (amulet.isType(objs.salve_amulet_i)) {
            attributes += CombatRangedAttributes.SalveAmuletI
        }

        val helmType = objTypes.getOrNull(helm)
        if (helmType != null && helmType.hasImbuedBlackMaskAttribute()) {
            attributes += CombatRangedAttributes.BlackMaskI
        }

        val weaponAttribute =
            when {
                EquipmentChecks.isTwistedBow(weapon) -> {
                    CombatRangedAttributes.TwistedBow
                }

                weapon.isAnyType(objs.craws_bow, objs.webweaver_bow) -> {
                    CombatRangedAttributes.RevenantWeapon
                }

                EquipmentChecks.isDragonHunterCrossbow(weapon) -> {
                    CombatRangedAttributes.DragonHunterCrossbow
                }

                weapon.isType(objs.scorching_bow) -> {
                    CombatRangedAttributes.ScorchingBow
                }

                weapon.isType(objs.bone_shortbow) -> {
                    CombatRangedAttributes.RatBoneWeapon
                }

                else -> null
            }

        if (weaponAttribute != null) {
            attributes += weaponAttribute
        }

        return attributes
    }

    private fun UnpackedObjType.hasImbuedBlackMaskAttribute(): Boolean {
        return param(params.blackmask_imbued) != 0 || param(params.slayer_helm_imbued) != 0
    }
}
