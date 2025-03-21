package org.rsmod.api.combat.formulas.attributes.collector

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.attributes.CombatRangedAttributes
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.front
import org.rsmod.api.player.hat
import org.rsmod.api.player.legs
import org.rsmod.api.player.righthand
import org.rsmod.api.player.torso
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isAnyType
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

public class CombatRangedAttributeCollector @Inject constructor(private val objTypes: ObjTypeList) {
    public fun collect(
        player: Player,
        attackType: RangedAttackType?,
    ): EnumSet<CombatRangedAttributes> {
        val attributes = EnumSet.noneOf(CombatRangedAttributes::class.java)

        if (attackType == RangedAttackType.Heavy) {
            attributes += CombatRangedAttributes.Heavy
        }

        val weapon = player.righthand
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

        val helmType = helm?.let(objTypes::get)
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

                weapon.isAnyType(
                    objs.dragon_hunter_crossbow,
                    objs.dragon_hunter_crossbow_t,
                    objs.dragon_hunter_crossbow_b,
                ) -> {
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
