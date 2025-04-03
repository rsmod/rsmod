package org.rsmod.api.spells

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.commons.magic.MagicSpellType
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.config.aliases.ParamInt
import org.rsmod.api.config.aliases.ParamObj
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.spells.configs.spell_enums
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.isType
import org.rsmod.game.type.util.EnumTypeMapResolver

public class MagicSpellRegistry
@Inject
constructor(private val objTypes: ObjTypeList, private val enumResolver: EnumTypeMapResolver) {
    private lateinit var objSpells: Map<Int, MagicSpell>
    private lateinit var autocastSpells: Map<Int, MagicSpell>

    /**
     * Convenience wrapper to look up the [ComponentType] associated with an [ObjType] spell.
     *
     * For example:
     * ```
     * val lumbridgeTeleportButton = spellRegistry.buttons[objs.spell_lumbridge_teleport]
     * ```
     *
     * This returns the component associated with the Lumbridge Teleport spell, or `null` if not
     * found.
     */
    public val buttons: ButtonLookup
        get() = ButtonLookup(objSpells)

    public fun getObjSpell(obj: ObjType): MagicSpell? = objSpells[obj.id]

    public fun getAutocastSpell(autocastId: Int): MagicSpell? = autocastSpells[autocastId]

    public fun combatSpells(): List<MagicSpell> =
        objSpells.values.filter { it.type == MagicSpellType.Combat }

    internal fun init() {
        check(!::objSpells.isInitialized) { "`init` already called for this repository." }

        val objSpells = loadObjSpells()
        this.objSpells = objSpells

        val autocastSpells = loadAutocastSpells(objSpells)
        this.autocastSpells = autocastSpells
    }

    private fun loadObjSpells(): Map<Int, MagicSpell> {
        val spells = hashMapOf<Int, MagicSpell>()

        val spellbookList = enumResolver[spell_enums.spellbooks].filterValuesNotNull()
        for (spellbookEnum in spellbookList.values) {
            val spellList = enumResolver[spellbookEnum].filterValuesNotNull()
            for (spellObj in spellList.values) {
                spells[spellObj.id] = spellObj.toMagicSpell()
            }
        }

        return spells
    }

    private fun loadAutocastSpells(objSpells: Map<Int, MagicSpell>): Map<Int, MagicSpell> {
        val spells = hashMapOf<Int, MagicSpell>()

        val autocastSpells = enumResolver[spell_enums.autocast_spells].filterValuesNotNull()
        for ((autocastId, spellObj) in autocastSpells) {
            val spell = objSpells[spellObj.id]
            checkNotNull(spell) { "Unexpected null spell for obj: $spellObj" }
            spells[autocastId] = spell
        }

        return spells
    }

    private fun ObjType.toMagicSpell(): MagicSpell {
        val unpacked = objTypes[this]

        // Some spells can have a default (-1) spellbook, such as `teleport_to_target_spell`.
        val spellbookId = unpacked.param(params.spell_spellbook)
        val spellbook = Spellbook[spellbookId]

        val spellTypeId = unpacked.param(params.spell_type)
        val spellType =
            MagicSpellType[spellTypeId]
                ?: error("Invalid MagicSpellType: $spellTypeId (spell=$unpacked)")

        val name = unpacked.param(params.spell_name)
        val button = unpacked.param(params.spell_button)
        val maxHit = unpacked.param(params.spell_maxhit)
        val levelReq = unpacked.param(params.spell_levelreq)
        val experience = unpacked.paramOrNull(params.spell_castxp)

        checkNotNull(experience) { "Cast xp not defined for spell obj: '$internalName' ($id)" }

        val objReqs = buildList {
            fun addRequirement(objParam: ParamObj, countParam: ParamInt) {
                val paramObj = unpacked.paramOrNull(objParam) ?: return
                val obj = paramObj.toRequirementObj()
                val worn = objTypes[obj].wearpos1.takeIf { it != -1 }
                val count = unpacked.param(countParam)
                check(worn == null || count == 1) {
                    "Count for worn objs expected to be 1: spell=$this, obj=$obj, count=$count"
                }
                this += MagicSpell.ObjRequirement(obj, count, worn)
            }
            addRequirement(params.spell_runetype_1, params.spell_runecount_1)
            addRequirement(params.spell_runetype_2, params.spell_runecount_2)
            addRequirement(params.spell_runetype_3, params.spell_runecount_3)
            addRequirement(params.spell_runetype_4, params.spell_runecount_4)
        }

        // For emulation purposes: Magic Dart has a quirk where obj validation order differs.
        // Although the staff appears first in the requirement list, its count is validated last.
        // Runes are always checked before the staff. This behavior appears to be unique to Magic
        // Dart, which is why we manually shift the staff to the end of the requirement list.
        val shiftFirstRequirementToTail = isType(objs.spell_magic_dart) && objReqs.isNotEmpty()

        val sortedObjReqs =
            if (shiftFirstRequirementToTail) {
                objReqs.drop(1) + objReqs.first()
            } else {
                objReqs
            }

        return MagicSpell(
            obj = this,
            name = name,
            component = button,
            spellbook = spellbook,
            type = spellType,
            maxHit = maxHit,
            levelReq = levelReq,
            castXp = experience / 10.0,
            objReqs = sortedObjReqs,
        )
    }

    // Claws of Guthix spell lists a special, non-usable staff obj (likely for visual purposes).
    // Since we use these objs for server-side validation, we replace it with the usable staff obj.
    private fun ObjType.toRequirementObj(): ObjType =
        if (isType(objs.guthix_staff_rune)) {
            objs.guthix_staff
        } else {
            this
        }

    public class ButtonLookup(private val spells: Map<Int, MagicSpell>) {
        public operator fun get(obj: ObjType): ComponentType? = spells[obj.id]?.component
    }
}
