package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvStackType

internal typealias equip_invs = EquipmentTabInvs

object EquipmentTabInvs : InvReferences() {
    val death_data = find("items_kept_on_death_data_transmission", 11353239)
    val death = find("items_kept_on_death", 11353355)
    val kept = find("items_kept_on_death_kept", 28603796)
}

internal object EquipmentTabInvEdit : InvEditor() {
    init {
        edit("items_kept_on_death_kept") { stack = InvStackType.Never }
    }
}
