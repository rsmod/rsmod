package org.rsmod.content.generic.npcs.cow

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.queue.QueueReferences

internal typealias cow_npcs = CowNpcs

internal typealias cow_queues = CowQueues

object CowNpcs : NpcReferences() {
    init {
        verify("cow_id_2790", 2721165461787568938)
        verify("cow_id_2791", 2721165461787568939)
        verify("cow_id_2793", 2721165461787568941)
        verify("cow_id_2795", 2721165461787568943)
        verify("cow_calf_id_2792", 1174981414091770174)
        verify("cow_calf_id_2794", 1174981414091770176)
    }

    val gillie_groats = find("gillie_groats", 5923782005100507456)
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
        calf("cow_calf_id_2792")
        calf("cow_calf_id_2794")
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
