package org.rsmod.api.combat.spells.autocast

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.api.combat.spells.autocast.configs.autocast_enums
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.util.EnumTypeMapResolver

@Singleton
public class AutocastSpells
@Inject
constructor(private val enumResolver: EnumTypeMapResolver, private val objTypes: ObjTypeList) {
    private lateinit var spells: Map<Int, ObjType>
    private lateinit var restricted: Set<Int>

    public operator fun get(autocastId: Int): ObjType? = spells[autocastId]

    public fun isRestrictedSpell(spell: ObjType): Boolean = spell.id in restricted

    internal fun startUp() {
        val spells = loadAutocastSpells()
        this.spells = spells

        val restricted = loadRestrictedSpells()
        this.restricted = restricted
    }

    private fun loadAutocastSpells(): Map<Int, ObjType> {
        val enum = enumResolver[autocast_enums.spells].filterValuesNotNull()
        return HashMap(enum.backing)
    }

    private fun loadRestrictedSpells(): Set<Int> {
        val enum = enumResolver[autocast_enums.restricted_spells].filterValuesNotNull()
        return enum.keys.map(ObjType::id).toHashSet()
    }
}
