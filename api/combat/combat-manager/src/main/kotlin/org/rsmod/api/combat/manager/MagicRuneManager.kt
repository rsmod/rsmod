package org.rsmod.api.combat.manager

import jakarta.inject.Inject
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
import org.rsmod.api.spells.runes.unlimited.UnlimitedRuneRepository
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isType

public class MagicRuneManager
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val fakes: FakeRuneRepository,
    private val combos: ComboRuneRepository,
    private val compact: CompactRuneRepository,
    private val unlimited: UnlimitedRuneRepository,
    private val staffSubs: StaffSubstituteRepository,
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
        val varbits = sources.filterIsInstance<MagicRunes.Source.VarBitSource>()
        val enoughVarBits = varbits.all { player.vars[it.varbit] >= it.count }
        if (!enoughVarBits) {
            return CastResult.Failure.NotEnoughVarBit
        }

        val invs = sources.filterIsInstance<MagicRunes.Source.InvSource>()
        val validatedObjs =
            invs.mapNotNull { (type, slot, count) ->
                val invObj = player.inv[slot]
                if (!invObj.isType(type) || invObj.count < count) {
                    return@mapNotNull null
                }
                invObj.copy(count = count)
            }

        val enoughObjs = validatedObjs.size == invs.size
        if (!enoughObjs) {
            return CastResult.Failure.NotEnoughInvObj
        }

        val transaction = player.invDelAll(player.inv, validatedObjs, strict = true)
        if (!transaction.success) {
            return CastResult.Failure.NotEnoughInvObj
        }

        for (source in varbits) {
            val result = player.vars[source.varbit] - source.count
            check(result >= 0) {
                "Expected result to be positive: " +
                    "$result (source=$source, validations=$validations)"
            }
            VarPlayerIntMapSetter.set(player, source.varbit, result)
        }

        val usedSunfire = validatedObjs.any { it.isType(objs.sunfire_rune) }
        return CastResult.Success.Consumed(usedSunfire)
    }

    public fun validateSpell(player: Player, spell: MagicSpell): Sequence<MagicRunes.Validation> =
        sequence {
            val runePack = validateRunePack(player, spell.obj)
            if (runePack != null) {
                yield(runePack)
                return@sequence
            }

            for (req in spell.objReqs) {
                val (obj, count, wornSlot) = req
                val validation =
                    if (wornSlot != null) {
                        validateWorn(player, obj, wornSlot)
                    } else {
                        validateRune(player, obj, count)
                    }
                yield(validation)
            }
        }

    public fun validateRunePack(player: Player, spell: ObjType): MagicRunes.Validation.Valid? =
        MagicRunes.validateRunePack(
            player = player,
            spell = spell,
            useFakeRunes = player.useFakeRunes(),
            allowBlighted = player.allowBlighted(),
        )

    public fun validateWorn(player: Player, obj: ObjType, wornSlot: Int): MagicRunes.Validation =
        MagicRunes.validateWorn(
            player = player,
            obj = obj,
            wornSlot = wornSlot,
            staffSubs = staffSubs,
        )

    public fun validateRune(player: Player, rune: ObjType, count: Int): MagicRunes.Validation =
        MagicRunes.validateRune(
            player = player,
            rune = rune,
            required = count,
            useFakeRunes = player.useFakeRunes(),
            runeFountain = player.nearFountainOfRune(),
            compact = compact,
            unlimited = unlimited,
            combos = combos,
            fakes = fakes,
        )

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
