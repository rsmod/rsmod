package org.rsmod.api.combat.formulas.maxhit.melee

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.AttackType
import org.rsmod.api.combat.formulas.EquipmentChecks
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatWornAttributes
import org.rsmod.api.combat.maxhit.player.PlayerMeleeMaxHit
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.combat.weapon.types.AttackTypes
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.npcs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isAnyType
import org.rsmod.game.obj.isType
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos

public class PvNMeleeMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val objTypes: ObjTypeList,
    private val attackStyles: AttackStyles,
    private val attackTypes: AttackTypes,
    private val weaponSpeeds: WeaponSpeeds,
    private val bonuses: WornBonuses,
) {
    private var Player.maxHit by intVarp(varps.com_maxhit)

    /**
     * Computes the maximum melee hit for [player] against [target], applying the
     * [specialMultiplier] before passing the result to [modifyPostSpec].
     *
     * **Notes:**
     * - This function should be used instead of [computeMaxHit] in most cases to ensure consistency
     *   in max hit calculations. Future optimizations, such as caching, may depend on this function
     *   as the main entry point.
     * - The `com_maxhit` varp for [player] is updated with the computed max hit.
     */
    public fun getMaxHit(player: Player, target: Npc, specialMultiplier: Double): Int {
        // Currently, we recalculate the max hit on every call to ensure the result reflects
        // the latest player state. If profiling shows this calculation becomes a performance
        // bottleneck, we can plan to optimize by using the cached `com_maxhit` varp while
        // adding safeguards to prevent stale data.
        val maxHit = computeMaxHit(player, target.visType, specialMultiplier)
        player.maxHit = maxHit
        return maxHit
    }

    public fun computeMaxHit(
        source: Player,
        target: UnpackedNpcType,
        specialMultiplier: Double,
    ): Int {
        val wornAttributes = collectWornAttributes(source)
        addProcAttributes(wornAttributes)

        val npcAttributes = collectNpcAttributes(target)
        if (target.isSlayerTask(source)) {
            npcAttributes += CombatNpcAttributes.SlayerTask
        }

        val modifiedDamage = computeModifiedDamage(source, wornAttributes, npcAttributes)
        val specMaxHit = (modifiedDamage * specialMultiplier).toInt()
        return modifyPostSpec(source, specMaxHit, wornAttributes, npcAttributes)
    }

    /**
     * Computes and returns the modified base damage **before** applying [modifyPostSpec] or any
     * special attack multipliers.
     *
     * This is particularly useful for attacks like the `Voidwaker` special, where the **Magic**
     * attack is based on the **Melee** max hit. In this case, the melee max hit is used as a base,
     * but damage reductions (such as from `Corporeal Beast`) are **not** applied.
     */
    public fun computeModifiedDamage(
        source: Player,
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val attackStyle = attackStyles.get(source)
        val meleeAttackStyle = MeleeAttackStyle.from(attackStyle)
        val effectiveStrength =
            MeleeMaxHitOperations.calculateEffectiveStrength(source, meleeAttackStyle)

        val strengthBonus = bonuses.strengthBonus(source)
        val baseDamage = PlayerMeleeMaxHit.calculateBaseDamage(effectiveStrength, strengthBonus)
        return modifyBaseDamage(baseDamage, wornAttributes, npcAttributes)
    }

    public fun modifyBaseDamage(
        baseDamage: Int,
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int = MeleeMaxHitOperations.modifyBaseDamage(baseDamage, wornAttributes, npcAttributes)

    public fun modifyPostSpec(
        source: Player,
        modifiedDamage: Int,
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val attackRate = weaponSpeeds.actual(source)
        val currHp = source.statMap.getCurrentLevel(stats.hitpoints).toInt()
        val maxHp = source.statMap.getBaseLevel(stats.hitpoints).toInt()
        return MeleeMaxHitOperations.modifyPostSpec(
            modifiedDamage,
            attackRate,
            currHp,
            maxHp,
            wornAttributes,
            npcAttributes,
        )
    }

    private fun collectWornAttributes(player: Player): EnumSet<CombatWornAttributes> {
        val wornAttributes = EnumSet.noneOf(CombatWornAttributes::class.java)

        val attackType = attackTypes.get(player)
        if (attackType == AttackType.Crush) {
            wornAttributes += CombatWornAttributes.Crush
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

                weapon.isAnyType(objs.dragon_hunter_lance, objs.dragon_hunter_wand) -> {
                    CombatWornAttributes.DragonHunterWeapon
                }

                weapon.isAnyType(
                    objs.keris,
                    objs.keris_p,
                    objs.keris_p_plus,
                    objs.keris_p_plus_plus,
                    objs.keris_partisan,
                    objs.keris_partisan_of_breaching,
                    objs.keris_partisan_of_corruption,
                    objs.keris_partisan_of_the_sun,
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

    private fun addProcAttributes(attribs: EnumSet<CombatWornAttributes>) {
        if (CombatWornAttributes.KerisWeapon in attribs && random.randomBoolean(51)) {
            attribs += CombatWornAttributes.KerisProc
        }

        if (CombatWornAttributes.Gadderhammer in attribs && random.randomBoolean(20)) {
            attribs += CombatWornAttributes.GadderhammerProc
        }
    }

    private fun collectNpcAttributes(type: UnpackedNpcType): EnumSet<CombatNpcAttributes> {
        val npcAttributes = EnumSet.noneOf(CombatNpcAttributes::class.java)

        // TODO(combat): "In wilderness" area check.

        val sizeAttribute =
            when (val size = type.size) {
                2 -> CombatNpcAttributes.Size2
                3 -> CombatNpcAttributes.Size3
                4 -> CombatNpcAttributes.Size4
                else -> {
                    if (size >= 5) {
                        CombatNpcAttributes.Size5OrMore
                    } else {
                        null
                    }
                }
            }

        if (sizeAttribute != null) {
            npcAttributes += sizeAttribute
        }

        if (type.param(params.revenant) != 0) {
            npcAttributes += CombatNpcAttributes.Revenant
        }

        if (type.param(params.undead) != 0) {
            npcAttributes += CombatNpcAttributes.Undead
        }

        if (type.param(params.demon) != 0) {
            npcAttributes += CombatNpcAttributes.Demon
        }

        if (type.param(params.demonbane_resistant) != 0) {
            npcAttributes += CombatNpcAttributes.DemonbaneResistance
        }

        if (type.param(params.draconic) != 0) {
            npcAttributes += CombatNpcAttributes.Draconic
        }

        if (type.param(params.kalphite) != 0) {
            npcAttributes += CombatNpcAttributes.Kalphite
        }

        if (type.param(params.golem) != 0) {
            npcAttributes += CombatNpcAttributes.Golem
        }

        if (type.param(params.leafy) != 0) {
            npcAttributes += CombatNpcAttributes.Leafy
        }

        if (type.param(params.rat) != 0) {
            npcAttributes += CombatNpcAttributes.Rat
        }

        if (type.param(params.shade) != 0) {
            npcAttributes += CombatNpcAttributes.Shade
        }

        if (type.param(params.tormented_demon) != 0 && !type.param(params.td_shield_active)) {
            npcAttributes += CombatNpcAttributes.TormentedDemonUnshielded
        }

        if (type.isType(npcs.corporeal_beast)) {
            npcAttributes += CombatNpcAttributes.CorporealBeast
        }

        return npcAttributes
    }

    private fun UnpackedNpcType.isSlayerTask(player: Player): Boolean {
        // TODO(combat): Resolve if type is slayer task.
        return false
    }
}
