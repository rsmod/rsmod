package org.rsmod.content.other.generic.npcs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor

internal object NpcEdits : NpcEditor() {
    init {
        people().forEach(::person)
    }

    private fun person(internal: String) {
        edit(internal) { contentType = content.person }
    }

    private fun people(): Set<String> =
        setOf(
            "man_id_3106",
            "man_id_3107",
            "man_id_3108",
            "man_id_6818",
            "woman_id_3111",
            "woman_id_3112",
            "woman_id_3113",
        )
}
