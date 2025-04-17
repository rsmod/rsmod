package org.rsmod.content.generic.npcs.cow

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.queue.QueueReferences

internal typealias cow_npcs = CowNpcs

internal typealias cow_queues = CowQueues

object CowNpcs : NpcReferences() {
    val gillie_the_milkmaid = find("gillie_the_milkmaid", 5923782005100507456)
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
        cow("cow")
        cow("cow2")
        cow("cow3")
        cow("cow_beef")
        calf("cow2_calf")
        calf("cow3_calf")
    }

    private fun cow(internal: String) {
        edit(internal) {
            contentGroup = content.cow
            timer = 1
        }
    }

    private fun calf(internal: String) {
        edit(internal) {
            contentGroup = content.cow_calf
            timer = 1
        }
    }
}
