package org.rsmod.api.combat.formulas.attributes.collector

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.attributes.CombatMeleeAttributes
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isAnyType
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos

public class CombatMeleeAttributeCollector @Inject constructor(private val objTypes: ObjTypeList) {
    public fun collect(
        player: Player,
        attackType: MeleeAttackType?,
    ): EnumSet<CombatMeleeAttributes> {
        val attributes = EnumSet.noneOf(CombatMeleeAttributes::class.java)

        if (attackType == MeleeAttackType.Crush) {
            attributes += CombatMeleeAttributes.Crush
        } else if (attackType == MeleeAttackType.Stab) {
            attributes += CombatMeleeAttributes.Stab
        }

        if (player.skullIcon == constants.skullicon_forinthry_surge) {
            attributes += CombatMeleeAttributes.ForinthrySurge
        }

        val amulet = player.worn[Wearpos.Front.slot]
        if (amulet.isType(objs.amulet_of_avarice)) {
            attributes += CombatMeleeAttributes.AmuletOfAvarice
        } else if (amulet.isAnyType(objs.salve_amulet_e, objs.salve_amulet_ei)) {
            attributes += CombatMeleeAttributes.SalveAmuletE
        } else if (amulet.isAnyType(objs.salve_amulet, objs.salve_amulet_i)) {
            attributes += CombatMeleeAttributes.SalveAmulet
        }

        val helm = player.worn[Wearpos.Hat.slot]
        val helmType = helm?.let(objTypes::get)
        if (helmType != null) {
            if (helmType.param(params.blackmask) != 0 || helmType.param(params.slayer_helm) != 0) {
                attributes += CombatMeleeAttributes.BlackMask
            }
        }

        val weapon = player.worn[Wearpos.RightHand.slot]
        if (weapon.isType(objs.arclight)) {
            attributes += CombatMeleeAttributes.Arclight
        } else if (weapon.isType(objs.burning_claws)) {
            attributes += CombatMeleeAttributes.BurningClaws
        }

        val top = player.worn[Wearpos.Torso.slot]
        val legs = player.worn[Wearpos.Legs.slot]
        if (EquipmentChecks.isObsidianSet(helm, top, legs)) {
            attributes += CombatMeleeAttributes.Obsidian
        }

        val weaponAttribute =
            when {
                weapon.isAnyType(
                    objs.toktz_xil_ek,
                    objs.toktz_xil_ak,
                    objs.tzhaar_ket_em,
                    objs.tzhaar_ket_om,
                    objs.tzhaar_ket_om_t,
                ) -> {
                    CombatMeleeAttributes.TzHaarWeapon
                }

                weapon.isType(objs.dragon_hunter_lance) -> {
                    CombatMeleeAttributes.DragonHunterLance
                }

                weapon.isType(objs.dragon_hunter_wand) -> {
                    CombatMeleeAttributes.DragonHunterWand
                }

                weapon.isType(objs.keris_partisan_of_breaching) -> {
                    CombatMeleeAttributes.KerisBreachPartisan
                }

                weapon.isType(objs.keris_partisan_of_the_sun) -> {
                    CombatMeleeAttributes.KerisSunPartisan
                }

                weapon.isAnyType(
                    objs.keris,
                    objs.keris_p,
                    objs.keris_p_plus,
                    objs.keris_p_plus_plus,
                    objs.keris_partisan,
                    objs.keris_partisan_of_corruption,
                ) -> {
                    CombatMeleeAttributes.KerisWeapon
                }

                weapon.isAnyType(objs.barronite_mace, objs.barronite_mace_l) -> {
                    CombatMeleeAttributes.BarroniteMaceWeapon
                }

                weapon.isAnyType(objs.viggoras_chainmace, objs.ursine_chainmace) -> {
                    CombatMeleeAttributes.RevenantWeapon
                }

                weapon.isType(objs.silverlight) -> {
                    CombatMeleeAttributes.Silverlight
                }

                weapon.isAnyType(
                    objs.leafbladed_sword,
                    objs.leafbladed_spear,
                    objs.leafbladed_battleaxe,
                ) -> {
                    CombatMeleeAttributes.LeafBladed
                }

                weapon.isType(objs.colossal_blade) -> {
                    CombatMeleeAttributes.ColossalBlade
                }

                weapon.isType(objs.bone_mace) -> {
                    CombatMeleeAttributes.RatBoneWeapon
                }

                weapon.isType(objs.inquisitors_mace) -> {
                    CombatMeleeAttributes.InquisitorWeapon
                }

                weapon.isAnyType(objs.osmumtens_fang, objs.osmumtens_fang_or) -> {
                    CombatMeleeAttributes.OsmumtensFang
                }

                weapon.isType(objs.gadderhammer) -> {
                    CombatMeleeAttributes.Gadderhammer
                }

                else -> null
            }

        if (weaponAttribute != null) {
            attributes += weaponAttribute
        }

        if (helm.isType(objs.inquisitors_great_helm)) {
            attributes += CombatMeleeAttributes.InquisitorHelm
        }

        if (top.isType(objs.inquisitors_hauberk)) {
            attributes += CombatMeleeAttributes.InquisitorTop
        }

        if (legs.isType(objs.inquisitors_plateskirt)) {
            attributes += CombatMeleeAttributes.InquisitorBottom
        }

        if (EquipmentChecks.isDharokSet(helm, top, legs, weapon)) {
            attributes += CombatMeleeAttributes.Dharoks
        }

        if (amulet.isAnyType(objs.berserker_necklace, objs.berserker_necklace_or)) {
            attributes += CombatMeleeAttributes.BerserkerNeck
        }

        val weaponType = weapon?.let(objTypes::get)
        if (weaponType != null && attackType == MeleeAttackType.Stab) {
            val isCorpbaneWeapon =
                weaponType.isCategoryType(categories.halberd) ||
                    weaponType.isCategoryType(categories.spear) ||
                    CombatMeleeAttributes.OsmumtensFang in attributes

            if (isCorpbaneWeapon) {
                attributes += CombatMeleeAttributes.CorpBaneWeapon
            }
        }

        return attributes
    }
}
