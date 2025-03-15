package org.rsmod.api.combat.formulas.attributes.collector

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.EquipmentChecks
import org.rsmod.api.combat.formulas.attributes.CombatWornAttributes
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isAnyType
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos

public class MeleeWornAttributeCollector @Inject constructor(private val objTypes: ObjTypeList) {
    public fun collect(
        player: Player,
        attackType: MeleeAttackType?,
    ): EnumSet<CombatWornAttributes> {
        val wornAttributes = EnumSet.noneOf(CombatWornAttributes::class.java)

        if (attackType == MeleeAttackType.Crush) {
            wornAttributes += CombatWornAttributes.Crush
        } else if (attackType == MeleeAttackType.Stab) {
            wornAttributes += CombatWornAttributes.Stab
        }

        if (player.skullIcon == constants.skullicon_forinthry_surge) {
            wornAttributes += CombatWornAttributes.ForinthrySurge
        }

        val amulet = player.worn[Wearpos.Front.slot]
        if (amulet.isType(objs.amulet_of_avarice)) {
            wornAttributes += CombatWornAttributes.AmuletOfAvarice
        } else if (amulet.isAnyType(objs.salve_amulet_e, objs.salve_amulet_ei)) {
            wornAttributes += CombatWornAttributes.SalveAmuletE
        } else if (amulet.isAnyType(objs.salve_amulet, objs.salve_amulet_i)) {
            wornAttributes += CombatWornAttributes.SalveAmulet
        }

        val helm = player.worn[Wearpos.Hat.slot]
        val helmType = helm?.let(objTypes::get)
        if (helmType != null) {
            if (helmType.param(params.blackmask) != 0 || helmType.param(params.slayer_helm) != 0) {
                wornAttributes += CombatWornAttributes.BlackMask
            }
        }

        val weapon = player.worn[Wearpos.RightHand.slot]
        if (weapon.isType(objs.arclight)) {
            wornAttributes += CombatWornAttributes.Arclight
        } else if (weapon.isType(objs.burning_claws)) {
            wornAttributes += CombatWornAttributes.BurningClaws
        }

        val top = player.worn[Wearpos.Torso.slot]
        val legs = player.worn[Wearpos.Legs.slot]
        if (EquipmentChecks.isObsidianSet(helm, top, legs)) {
            wornAttributes += CombatWornAttributes.Obsidian
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
                    CombatWornAttributes.TzHaarWeapon
                }

                weapon.isType(objs.dragon_hunter_lance) -> {
                    CombatWornAttributes.DragonHunterLance
                }

                weapon.isType(objs.dragon_hunter_wand) -> {
                    CombatWornAttributes.DragonHunterWand
                }

                weapon.isType(objs.keris_partisan_of_breaching) -> {
                    CombatWornAttributes.KerisBreachPartisan
                }

                weapon.isType(objs.keris_partisan_of_the_sun) -> {
                    CombatWornAttributes.KerisSunPartisan
                }

                weapon.isAnyType(
                    objs.keris,
                    objs.keris_p,
                    objs.keris_p_plus,
                    objs.keris_p_plus_plus,
                    objs.keris_partisan,
                    objs.keris_partisan_of_corruption,
                ) -> {
                    CombatWornAttributes.KerisWeapon
                }

                weapon.isAnyType(objs.barronite_mace, objs.barronite_mace_l) -> {
                    CombatWornAttributes.BarroniteMaceWeapon
                }

                weapon.isAnyType(objs.viggoras_chainmace, objs.ursine_chainmace) -> {
                    CombatWornAttributes.RevenantMeleeWeapon
                }

                weapon.isType(objs.silverlight) -> {
                    CombatWornAttributes.Silverlight
                }

                weapon.isAnyType(
                    objs.leafbladed_sword,
                    objs.leafbladed_spear,
                    objs.leafbladed_battleaxe,
                ) -> {
                    CombatWornAttributes.LeafBladed
                }

                weapon.isType(objs.colossal_blade) -> {
                    CombatWornAttributes.ColossalBlade
                }

                weapon.isType(objs.bone_mace) -> {
                    CombatWornAttributes.RatBoneWeapon
                }

                weapon.isType(objs.inquisitors_mace) -> {
                    CombatWornAttributes.InquisitorWeapon
                }

                weapon.isAnyType(objs.osmumtens_fang, objs.osmumtens_fang_or) -> {
                    CombatWornAttributes.OsmumtensFang
                }

                weapon.isType(objs.gadderhammer) -> {
                    CombatWornAttributes.Gadderhammer
                }

                else -> null
            }

        if (weaponAttribute != null) {
            wornAttributes += weaponAttribute
        }

        if (helm.isType(objs.inquisitors_great_helm)) {
            wornAttributes += CombatWornAttributes.InquisitorHelm
        }

        if (top.isType(objs.inquisitors_hauberk)) {
            wornAttributes += CombatWornAttributes.InquisitorTop
        }

        if (legs.isType(objs.inquisitors_plateskirt)) {
            wornAttributes += CombatWornAttributes.InquisitorBottom
        }

        if (EquipmentChecks.isDharokSet(helm, top, legs, weapon)) {
            wornAttributes += CombatWornAttributes.Dharoks
        }

        if (amulet.isAnyType(objs.berserker_necklace, objs.berserker_necklace_or)) {
            wornAttributes += CombatWornAttributes.BerserkerNeck
        }

        val weaponType = weapon?.let(objTypes::get)
        if (weaponType != null && weaponType.param(params.corpbane) != 0) {
            wornAttributes += CombatWornAttributes.CorpBaneWeapon
        }

        return wornAttributes
    }
}
