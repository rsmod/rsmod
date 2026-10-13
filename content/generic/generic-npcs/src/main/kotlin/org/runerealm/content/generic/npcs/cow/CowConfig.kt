package org.runerealm.content.generic.npcs.cow

import org.runerealm.api.config.refs.content
import org.runerealm.api.type.editors.loc.LocEditor
import org.runerealm.api.type.editors.npc.NpcEditor
import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.api.type.refs.npc.NpcReferences
import org.runerealm.api.type.refs.queue.QueueReferences
import org.runerealm.game.type.npc.NpcType

internal typealias cow_npcs = CowNpcs

internal typealias cow_queues = CowQueues

internal typealias cow_locs = CowLocs

object CowNpcs : NpcReferences() {
    val gillie_the_milkmaid = find("gillie_the_milkmaid", 8262063475332109549)
    val cow = find("cow")
    val cow2 = find("cow2")
    val cow3 = find("cow3")
    val cow_beef = find("cow_beef")
    val cow2_calf = find("cow2_calf")
    val cow3_calf = find("cow3_calf")
}

object CowQueues : QueueReferences() {
    val milk = find("milk_cow")
}

internal object CowLocs : LocReferences() {
    val fat_cow = find("fat_cow")
}

internal object CowLocEdits : LocEditor() {
    init {
        edit(cow_locs.fat_cow) { contentGroup = content.dairy_cow }
    }
}

internal object CowNpcEdits : NpcEditor() {
    init {
        cow(cow_npcs.cow)
        cow(cow_npcs.cow2)
        cow(cow_npcs.cow3)
        cow(cow_npcs.cow_beef)
        calf(cow_npcs.cow2_calf)
        calf(cow_npcs.cow3_calf)
    }

    private fun cow(type: NpcType) {
        edit(type) {
            contentGroup = content.cow
            timer = 1
        }
    }

    private fun calf(type: NpcType) {
        edit(type) {
            contentGroup = content.cow_calf
            timer = 1
        }
    }
}
