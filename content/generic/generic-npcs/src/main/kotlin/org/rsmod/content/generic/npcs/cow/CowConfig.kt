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
        verify("cow_id_2790", 9209685349162039084)
        verify("cow_id_2791", 9209685349162039085)
        verify("cow_id_2793", 9209685349162039087)
        verify("cow_id_2795", 9193767768860587612)
        verify("cow_calf_id_2792", 7126175047278744087)
        verify("cow_calf_id_2794", 7126175047278744089)
    }

    val gillie_groats = find("gillie_groats", 1639192872950274564)
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
