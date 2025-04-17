package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvStackType

internal typealias equip_invs = EquipmentTabInvs

object EquipmentTabInvs : InvReferences() {
    // TODO: Fix incorrect inv types.
    val death_data = find("diango_hols_sack", 11353239)
    val death = find("deathkeep_items", 11353355)
    val kept = find("skill_guide_hunting_tracking", 28603796)
}

internal object EquipmentTabInvEdit : InvEditor() {
    init {
        edit(equip_invs.kept) { stack = InvStackType.Never }
    }
}
