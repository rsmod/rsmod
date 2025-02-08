package org.rsmod.content.other.generic.npcs.cow

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.queue.QueueReferences

internal typealias cow_npcs = CowNpcs

internal typealias cow_queues = CowQueues

object CowNpcs : NpcReferences() {
    val gillie_groats = find("gillie_groats")
}

object CowQueues : QueueReferences() {
    val milk = find("milk_cow")
}

internal object CowLocEdits : LocEditor() {
    init {
        edit("dairy_cow") { contentGroup = content.dairy_cow }
    }
}

internal object CowNpcEdits : NpcEditor() {
    init {
        cow("cow_id_2790")
        cow("cow_id_2791")
        cow("cow_id_2793")
        cow("cow_id_2795")
    }

    private fun cow(internal: String) {
        edit(internal) { contentGroup = content.cow }
    }
}
