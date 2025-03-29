package org.rsmod.api.spells.runes

import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.spells.runes.combo.ComboRuneRepository
import org.rsmod.api.spells.runes.compact.CompactRuneRepository
import org.rsmod.api.spells.runes.fake.FakeRuneRepository
import org.rsmod.api.spells.runes.staves.StaffSubstituteRepository
import org.rsmod.api.spells.runes.unlimited.UnlimitedRuneRepository
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isAnyType
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.obj.isAnyType
import org.rsmod.game.type.obj.isType
import org.rsmod.game.type.varbit.VarBitType

public object MagicRunes {
    public fun validateWorn(
        player: Player,
        obj: ObjType,
        wornSlot: Int,
        staffSubs: StaffSubstituteRepository,
    ): Validation {
        val worn = player.worn[wornSlot]
        return when {
            worn.isType(obj) -> Validation.Valid.Unlimited
            worn != null && staffSubs.isValidSubstitute(obj, worn) -> Validation.Valid.Unlimited
            else -> Validation.Invalid.NotWearing(obj)
        }
    }

    public fun validateRune(
        player: Player,
        rune: ObjType,
        required: Int,
        useFakeRunes: Boolean,
        runeFountain: Boolean,
        compact: CompactRuneRepository,
        unlimited: UnlimitedRuneRepository,
        combos: ComboRuneRepository,
        fakes: FakeRuneRepository,
    ): Validation {
        val compactId = compact[rune]

        // Note: There are other conditions that should be handled here alongside `runeFountain`,
        // but their use cases are currently unclear. We will skip them for now until more
        // information becomes available.
        if (compactId != null && runeFountain) {
            return Validation.Valid.Unlimited
        }

        val righthand = player.worn[Wearpos.RightHand.slot]
        val lefthand = player.worn[Wearpos.LeftHand.slot]

        if (unlimited.hasHighPrioritySource(rune, righthand, lefthand)) {
            return Validation.Valid.Unlimited
        }

        if (useFakeRunes) {
            val fake = fakes[rune]
            if (fake != null) {
                val invFakeSlot = player.inv.indexOfFirst { it.isType(fake) }
                val invFakeRune = player.inv[invFakeSlot]
                val count = invFakeRune?.count ?: 0
                return if (count >= required) {
                    val sources = listOf(Source.InvSource(fake, invFakeSlot, required))
                    Validation.Valid.HasEnough(sources)
                } else {
                    Validation.Invalid.NotEnoughRunes(rune)
                }
            }
        }

        if (unlimited.hasLowPrioritySource(rune, righthand, lefthand)) {
            return Validation.Valid.Unlimited
        }

        val invRuneSlot = player.inv.indexOfFirst { it.isType(rune) }
        val invRuneObj = player.inv[invRuneSlot]

        // Fast-path: return single-list `HasEnough` validation when inv has enough runes.
        if (invRuneObj != null && invRuneObj.count >= required) {
            val sources = listOf(Source.InvSource(rune, invRuneSlot, required))
            return Validation.Valid.HasEnough(sources)
        }

        val sources = mutableListOf<Source>()
        var remaining = required

        // Reduce remaining count from existing inv rune (if available).
        if (invRuneObj != null) {
            remaining -= invRuneObj.count
            sources += Source.InvSource(rune, invRuneSlot, invRuneObj.count)
        }

        // Reduce remaining count from available combo runes (if applicable).
        val comboRunes = combos[rune]
        for (comboRune in comboRunes) {
            val comboSlot = player.inv.indexOfFirst { it.isType(comboRune) }
            val comboObj = player.inv[comboSlot] ?: continue
            if (comboObj.count >= remaining) {
                sources += Source.InvSource(comboRune, comboSlot, remaining)
                return Validation.Valid.HasEnough(sources)
            }
            remaining -= comboObj.count
            sources += Source.InvSource(comboRune, comboSlot, comboObj.count)
        }

        // If player does not have a rune pouch in inv, we can return early with a failure result.
        val hasRunePouch = player.inv.any { it.isRegularRunePouch() || it.isDivineRunePouch() }
        if (!hasRunePouch) {
            return Validation.Invalid.NotEnoughRunes(rune)
        }
        val hasDivineRunePouch = player.inv.any { it.isDivineRunePouch() }

        val pouchCompactRune1 = player.vars[varbits.rune_pouch_compactid1]
        val pouchCompactRune2 = player.vars[varbits.rune_pouch_compactid2]
        val pouchCompactRune3 = player.vars[varbits.rune_pouch_compactid3]
        val pouchCompactRune4 =
            if (hasDivineRunePouch) {
                player.vars[varbits.rune_pouch_compactid4]
            } else {
                null
            }

        val pouchRuneCountVarBit =
            when {
                compact.matches(pouchCompactRune1, rune, comboRunes) -> varbits.rune_pouch_count1
                compact.matches(pouchCompactRune2, rune, comboRunes) -> varbits.rune_pouch_count2
                compact.matches(pouchCompactRune3, rune, comboRunes) -> varbits.rune_pouch_count3
                compact.matches(pouchCompactRune4, rune, comboRunes) -> varbits.rune_pouch_count4
                else -> null
            }

        val pouchRuneCount =
            if (pouchRuneCountVarBit != null) player.vars[pouchRuneCountVarBit] else 0

        if (pouchRuneCountVarBit != null && pouchRuneCount >= remaining) {
            sources += Source.VarBitSource(pouchRuneCountVarBit, remaining)
            return Validation.Valid.HasEnough(sources)
        }

        return Validation.Invalid.NotEnoughRunes(rune)
    }

    // Note: We _could_ use an enum config and repository to define these "rune packs," but since
    // they do not change often, it would likely be overengineering at this point. If the need
    // arises later, we can refactor without much effort.
    public fun validateRunePack(
        player: Player,
        spell: ObjType,
        useFakeRunes: Boolean,
        allowBlighted: Boolean,
    ): Validation.Valid? {
        val inv = player.inv
        when {
            spell.isAnyType(objs.spell_blood_blitz, objs.spell_blood_barrage) -> {
                if (useFakeRunes && objs.lms_rune_pouch in inv) {
                    return Validation.Valid.Unlimited
                }
            }
            spell.isAnyType(objs.spell_ice_blitz, objs.spell_ice_barrage) -> {
                if (useFakeRunes && objs.lms_rune_pouch in inv) {
                    return Validation.Valid.Unlimited
                }
                val sack = objs.blighted_ancient_ice_sack
                if (allowBlighted && sack in inv) {
                    val invSlot = inv.indexOfFirst { it.isType(sack) }
                    val source = Source.InvSource(sack, invSlot, count = 1)
                    return Validation.Valid.HasEnough(listOf(source))
                }
            }
            spell.isAnyType(objs.spell_ice_rush, objs.spell_ice_burst) -> {
                val sack = objs.blighted_ancient_ice_sack
                if (allowBlighted && sack in inv) {
                    val invSlot = inv.indexOfFirst { it.isType(sack) }
                    val source = Source.InvSource(sack, invSlot, count = 1)
                    return Validation.Valid.HasEnough(listOf(source))
                }
            }
            spell.isAnyType(objs.spell_bind, objs.spell_snare) -> {
                val sack = objs.blighted_entangle_sack
                if (allowBlighted && sack in inv) {
                    val invSlot = inv.indexOfFirst { it.isType(sack) }
                    val source = Source.InvSource(sack, invSlot, count = 1)
                    return Validation.Valid.HasEnough(listOf(source))
                }
            }
            spell.isType(objs.spell_entangle) -> {
                if (useFakeRunes && objs.lms_rune_pouch in inv) {
                    return Validation.Valid.Unlimited
                }
                val sack = objs.blighted_entangle_sack
                if (allowBlighted && sack in inv) {
                    val invSlot = inv.indexOfFirst { it.isType(sack) }
                    val source = Source.InvSource(sack, invSlot, count = 1)
                    return Validation.Valid.HasEnough(listOf(source))
                }
            }
            spell.isAnyType(objs.spell_tele_block, objs.spell_teleport_to_target) -> {
                val sack = objs.blighted_teleport_spell_sack
                if (allowBlighted && sack in inv) {
                    val invSlot = inv.indexOfFirst { it.isType(sack) }
                    val source = Source.InvSource(sack, invSlot, count = 1)
                    return Validation.Valid.HasEnough(listOf(source))
                }
            }
            spell.isType(objs.spell_cure_me) -> {
                if (useFakeRunes && objs.lms_rune_pouch in inv) {
                    return Validation.Valid.Unlimited
                }
            }
            spell.isType(objs.spell_vengeance_other) -> {
                val sack = objs.blighted_vengeance_sack
                if (allowBlighted && sack in inv) {
                    val invSlot = inv.indexOfFirst { it.isType(sack) }
                    val source = Source.InvSource(sack, invSlot, count = 1)
                    return Validation.Valid.HasEnough(listOf(source))
                }
            }
            spell.isType(objs.spell_vengeance) -> {
                if (useFakeRunes && objs.lms_rune_pouch in inv) {
                    return Validation.Valid.Unlimited
                }
                val sack = objs.blighted_vengeance_sack
                if (allowBlighted && sack in inv) {
                    val invSlot = inv.indexOfFirst { it.isType(sack) }
                    val source = Source.InvSource(sack, invSlot, count = 1)
                    return Validation.Valid.HasEnough(listOf(source))
                }
            }
            spell.isAnyType(
                objs.spell_wind_wave,
                objs.spell_water_wave,
                objs.spell_earth_wave,
                objs.spell_fire_wave,
            ) -> {
                val sack = objs.blighted_surge_sack
                if (allowBlighted && sack in inv) {
                    val invSlot = inv.indexOfFirst { it.isType(sack) }
                    val source = Source.InvSource(sack, invSlot, count = 1)
                    return Validation.Valid.HasEnough(listOf(source))
                }
            }
            spell.isAnyType(
                objs.spell_wind_surge,
                objs.spell_water_surge,
                objs.spell_earth_surge,
                objs.spell_fire_surge,
            ) -> {
                if (useFakeRunes && objs.lms_rune_pouch in inv) {
                    return Validation.Valid.Unlimited
                }
                val sack = objs.blighted_surge_sack
                if (allowBlighted && sack in inv) {
                    val invSlot = inv.indexOfFirst { it.isType(sack) }
                    val source = Source.InvSource(sack, invSlot, count = 1)
                    return Validation.Valid.HasEnough(listOf(source))
                }
            }
        }
        return null
    }

    private fun InvObj?.isRegularRunePouch(): Boolean =
        isAnyType(objs.rune_pouch, objs.rune_pouch_l)

    private fun InvObj?.isDivineRunePouch(): Boolean =
        isAnyType(objs.divine_rune_pouch, objs.divine_rune_pouch_l)

    public sealed class Validation {
        public val isValid: Boolean
            get() = this is Valid

        public val isInvalid: Boolean
            get() = this is Invalid

        public sealed class Valid : Validation() {
            public data object Unlimited : Valid()

            public data class HasEnough(val sources: List<Source>) : Valid()
        }

        public sealed class Invalid : Validation() {
            public data class NotEnoughRunes(val obj: ObjType) : Invalid()

            public data class NotWearing(val obj: ObjType) : Invalid()
        }
    }

    public sealed class Source {
        public data class InvSource(val obj: ObjType, val slot: Int, val count: Int) : Source()

        public data class VarBitSource(val varbit: VarBitType, val count: Int) : Source()
    }
}
