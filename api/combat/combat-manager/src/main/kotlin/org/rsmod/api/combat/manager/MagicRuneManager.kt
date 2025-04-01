package org.rsmod.api.combat.manager

import jakarta.inject.Inject
import kotlin.collections.any
import kotlin.contracts.contract
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.invtx.invDelAll
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.stat.magicLvl
import org.rsmod.api.player.vars.VarPlayerIntMapSetter
import org.rsmod.api.player.vars.enumVarBit
import org.rsmod.api.spells.runes.MagicRunes
import org.rsmod.api.spells.runes.combo.ComboRuneRepository
import org.rsmod.api.spells.runes.compact.CompactRuneRepository
import org.rsmod.api.spells.runes.fake.FakeRuneRepository
import org.rsmod.api.spells.runes.staves.StaffSubstituteRepository
import org.rsmod.api.spells.runes.subs.RuneSubstituteRepository
import org.rsmod.api.spells.runes.unlimited.UnlimitedRuneRepository
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isAnyType
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isType
import org.rsmod.game.type.varbit.VarBitType

public class MagicRuneManager
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val fakes: FakeRuneRepository,
    private val combos: ComboRuneRepository,
    private val compact: CompactRuneRepository,
    private val unlimited: UnlimitedRuneRepository,
    private val staffSubs: StaffSubstituteRepository,
    private val runeSubs: RuneSubstituteRepository,
) {
    private val Player.spellbook by enumVarBit<Spellbook>(varbits.spellbook)

    /**
     * Attempts to cast [spell] by first verifying the player meets all requirements, and then
     * consuming the necessary objs and/or varp values.
     *
     * This combines the checks from [validateAccess] and the obj requirement check and consumption
     * logic from [delReqs]. If any requirement is not met or cannot be consumed, the appropriate
     * failure message is sent to the player, and a failure result is returned.
     *
     * _This function does **not** perform the actual effects of the spell._
     *
     * @return A [CastResult] indicating whether the requirements were successfully removed. Returns
     *   [CastResult.Success] if all objs and/or vars were removed, or [CastResult.Failure] if
     *   something went wrong.
     */
    public fun attemptCast(
        player: Player,
        spell: MagicSpell,
        book: Spellbook = player.spellbook,
    ): CastResult {
        val accessFailure = validateAccess(player, spell, book)
        if (accessFailure != null) {
            return accessFailure
        }
        return delReqs(player, spell)
    }

    /**
     * Returns `true` if the player meets all requirements needed to cast [spell], and if [book]
     * matches the spell's required [MagicSpell.spellbook].
     *
     * **Note:** This does **not** consume any objs or modify varp values (e.g., rune pouch
     * quantities). To validate and consume the requirements, use [attemptCast] instead; or call
     * [delReqs] to explicitly consume the requirements.
     */
    public fun canCastSpell(
        player: Player,
        spell: MagicSpell,
        book: Spellbook = player.spellbook,
    ): Boolean {
        val accessFailure = validateAccess(player, spell, book)
        if (accessFailure != null) {
            return false
        }
        return hasRunes(player, spell)
    }

    /**
     * Returns `true` if the player has all the consumable requirements needed to cast [spell].
     * Otherwise, sends the appropriate missing-requirement message to [player] and returns `false`.
     */
    public fun hasRunes(player: Player, spell: MagicSpell): Boolean {
        val invalid = validateSpell(player, spell).firstOrNull(MagicRunes.Validation::isInvalid)
        if (invalid != null) {
            val message = invalid.requirementMessage()
            player.mes(message)
            return false
        }
        return true
    }

    /**
     * Attempts to delete all objs required to cast [spell], as defined by [MagicSpell.objReqs].
     *
     * If one or more requirements cannot be removed, the appropriate message is sent to [player],
     * and this function returns [CastResult.Failure].
     *
     * This function delegates to [delAll], which includes safeguards to ensure that the player has
     * enough of each requirement before any removals occur.
     *
     * @return A [CastResult] indicating whether the requirements were successfully removed. Returns
     *   [CastResult.Success] if all objs and/or vars were removed, or [CastResult.Failure] if
     *   something went wrong.
     */
    public fun delReqs(player: Player, spell: MagicSpell): CastResult {
        val validations = validateSpell(player, spell).toList()
        val valid = validations.filterIsInstance<MagicRunes.Validation.Valid>()

        val allValid = valid.size == validations.size
        if (!allValid) {
            val invalid = validations.firstOrNull(MagicRunes.Validation::isInvalid)
            if (invalid != null) {
                val message = invalid.requirementMessage()
                player.mes(message)
            }
            return CastResult.Failure.MissingObjRequirements
        }

        return delAll(player, valid)
    }

    private fun delAll(player: Player, validations: List<MagicRunes.Validation.Valid>): CastResult {
        require(validations.isNotEmpty()) { "`validations` should not be empty." }

        val sourced = validations.filterIsInstance<MagicRunes.Validation.Valid.HasEnough>()

        // Since `HasEnough` and `Unlimited` are the only possible `Valid` types, if `sourced` is
        // empty, then all validations must be `Unlimited`. In that case, we don't need to delete
        // anything, so we can return `true` early.
        val allUnlimitedSources = sourced.isEmpty()
        if (allUnlimitedSources) {
            return CastResult.Success.AllUnlimited
        }

        val sources = sourced.flatMap { it.sources }

        // Ensure player has enough varbits to be removed.
        val varbitSources = sources.filterIsInstance<MagicRunes.Source.VarBitSource>()
        val enoughVarBits = varbitSources.all { player.vars[it.varbit] >= it.count }
        if (!enoughVarBits) {
            return CastResult.Failure.NotEnoughVarBit
        }

        val invSources = sources.filterIsInstance<MagicRunes.Source.InvSource>()

        val consume = ArrayList<InvObj>(invSources.size)
        for (source in invSources) {
            val (type, slot, required) = source
            val invObj = player.inv[slot]
            if (!invObj.isType(type)) {
                continue
            }
            consume += invObj.copy(count = required)
        }

        val transaction = player.invDelAll(player.inv, consume, strict = true)
        if (!transaction.success) {
            return CastResult.Failure.NotEnoughInvObj
        }

        for (source in varbitSources) {
            val result = player.vars[source.varbit] - source.count
            check(result >= 0) {
                "Expected result to be positive: " +
                    "$result (source=$source, validations=$validations)"
            }
            VarPlayerIntMapSetter.set(player, source.varbit, result)
        }

        val usedSunfire = consume.any { it.isType(objs.sunfire_rune) }
        return CastResult.Success.Consumed(usedSunfire)
    }

    public fun validateSpell(player: Player, spell: MagicSpell): List<MagicRunes.Validation> {
        val runePack = validateRunePack(player, spell.obj)
        return if (runePack != null) {
            listOf(runePack)
        } else {
            validateReqs(player, spell.objReqs)
        }
    }

    private fun validateRunePack(player: Player, spell: ObjType): MagicRunes.Validation.Valid? =
        MagicRunes.validateRunePack(
            player = player,
            spell = spell,
            useFakeRunes = player.useFakeRunes(),
            allowBlighted = player.allowBlighted(),
        )

    private fun validateReqs(
        player: Player,
        reqs: List<MagicSpell.ObjRequirement>,
    ): List<MagicRunes.Validation> =
        MagicRunes.validateRequirements(
            inv = player.inv,
            worn = player.worn,
            pouch = player.currentRunePouch(),
            requirements = MagicRunes.RequirementList.from(reqs),
            useFakeRunes = player.useFakeRunes(),
            runeFountain = player.nearFountainOfRune(),
            compact = compact,
            unlimited = unlimited,
            combos = combos,
            fakes = fakes,
            runeSubs = runeSubs,
            staffSubs = staffSubs,
        )

    /**
     * Verifies that the player meets the magic level requirement to cast [spell], and that the
     * provided [book] matches the spell's [MagicSpell.spellbook].
     */
    private fun validateAccess(
        player: Player,
        spell: MagicSpell,
        book: Spellbook,
    ): CastResult.Failure? {
        if (spell.spellbook != book) {
            // Official behavior: This does not send a message - it simply no-ops.
            return CastResult.Failure.IncorrectSpellbook
        }
        if (player.magicLvl < spell.levelReq) {
            player.mes("Your Magic level is not high enough for this spell.")
            return CastResult.Failure.MissingLevelRequirement
        }
        return null
    }

    private fun Player.useFakeRunes(): Boolean {
        return vars[varbits.in_ba_game] == 1 || vars[varbits.in_lms_game] == 1
    }

    private fun Player.allowBlighted(): Boolean {
        return vars[varbits.allow_blighted_sacks] == 1
    }

    private fun Player.nearFountainOfRune(): Boolean {
        return vars[varbits.fountain_of_rune] == 1
    }

    private fun Player.currentRunePouch(): MagicRunes.RunePouch? {
        val hasRunePouch = inv.any { it.isRegularRunePouch() || it.isDivineRunePouch() }
        if (!hasRunePouch) {
            return null
        }
        val hasDivineRunePouch = inv.any { it.isDivineRunePouch() }

        val pouchCompactRune1 = vars[varbits.rune_pouch_compactid1]
        val pouchCompactRune2 = vars[varbits.rune_pouch_compactid2]
        val pouchCompactRune3 = vars[varbits.rune_pouch_compactid3]
        val pouchCountVarBit1 = varbits.rune_pouch_count1
        val pouchCountVarBit2 = varbits.rune_pouch_count2
        val pouchCountVarBit3 = varbits.rune_pouch_count3

        val pouchCompactRune4: Int
        val pouchCountVarBit4: VarBitType?
        if (hasDivineRunePouch) {
            pouchCompactRune4 = vars[varbits.rune_pouch_compactid4]
            pouchCountVarBit4 = varbits.rune_pouch_count4
        } else {
            pouchCompactRune4 = 0
            pouchCountVarBit4 = null
        }

        return MagicRunes.RunePouch(
            compactId1 = pouchCompactRune1,
            compactId2 = pouchCompactRune2,
            compactId3 = pouchCompactRune3,
            compactId4 = pouchCompactRune4,
            countVarBit1 = pouchCountVarBit1,
            countVarBit2 = pouchCountVarBit2,
            countVarBit3 = pouchCountVarBit3,
            countVarBit4 = pouchCountVarBit4,
            count1 = vars[pouchCountVarBit1],
            count2 = vars[pouchCountVarBit2],
            count3 = vars[pouchCountVarBit3],
            count4 = pouchCountVarBit4?.let(vars::get) ?: 0,
        )
    }

    private fun InvObj?.isRegularRunePouch(): Boolean =
        isAnyType(objs.rune_pouch, objs.rune_pouch_l)

    private fun InvObj?.isDivineRunePouch(): Boolean =
        isAnyType(objs.divine_rune_pouch, objs.divine_rune_pouch_l)

    private fun MagicRunes.Validation.requirementMessage(): String =
        when (this) {
            is MagicRunes.Validation.Invalid.NotEnoughRunes -> {
                objTypes[obj].runeRequirementMessage()
            }
            is MagicRunes.Validation.Invalid.NotWearing -> {
                objTypes[obj].wornRequirementMessage()
            }
            else -> "You do not have enough runes to cast this spell."
        }

    private fun UnpackedObjType.runeRequirementMessage(): String {
        if (isCategoryType(categories.rune)) {
            val name = name.dropLast(5) + " Runes"
            return "You do not have enough $name to cast this spell."
        }
        val name = if (name.endsWith('s')) name else "${name}s"
        return "You do not have enough ${name.lowercase()} to cast this spell."
    }

    private fun UnpackedObjType.wornRequirementMessage(): String {
        val custom = paramOrNull(params.spell_worn_req_message)
        return when {
            custom != null -> custom
            isType(objs.ibans_staff) -> {
                "You must wield Iban's Staff to cast this spell."
            }
            isType(objs.saradomin_staff) -> {
                "You must be wielding the Staff of Saradomin or the " +
                    "Staff of Light to cast this spell."
            }
            isType(objs.zamorak_staff) -> {
                "You must be wielding the Staff of Zamorak, Staff of the " +
                    "Dead, Thammaron's Sceptre, or the Accursed Sceptre to " +
                    "cast this spell."
            }
            isType(objs.guthix_staff) -> {
                "You must wield the Staff of Guthix, Staff of Balance " +
                    "or the Void Knight Mace to cast this spell."
            }
            isType(objs.slayers_staff) -> {
                "You need to be wielding a suitable staff to cast this spell."
            }
            else -> "You need a $name to cast this spell."
        }
    }

    public sealed class CastResult {
        public sealed class Success : CastResult() {
            public object AllUnlimited : Success()

            public data class Consumed(val usedSunfire: Boolean) : Success()
        }

        public sealed class Failure : CastResult() {
            public object NotEnoughVarBit : Failure()

            public object NotEnoughInvObj : Failure()

            /**
             * Unlike [NotEnoughVarBit] and [NotEnoughInvObj], this failure occurs before any
             * consumption is attempted. It indicates a lower-level validation failure from
             * [MagicRunes].
             */
            public object MissingObjRequirements : Failure()

            public object MissingLevelRequirement : Failure()

            public object IncorrectSpellbook : Failure()
        }
    }

    public companion object {
        public fun CastResult.isFailure(): Boolean {
            contract { returns(true) implies (this@isFailure is CastResult.Failure) }
            return this is CastResult.Failure
        }

        public fun CastResult.isSuccess(): Boolean {
            contract { returns(true) implies (this@isSuccess is CastResult.Success) }
            return this is CastResult.Success
        }

        public fun CastResult.consumedRune(): Boolean {
            contract { returns(true) implies (this@consumedRune is CastResult.Success.Consumed) }
            return this is CastResult.Success.Consumed
        }
    }
}
