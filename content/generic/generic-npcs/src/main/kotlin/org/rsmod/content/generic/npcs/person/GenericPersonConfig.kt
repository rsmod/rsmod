package org.rsmod.content.generic.npcs.person

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor

private val people = setOf("man", "man2", "man3", "man_indoor", "woman", "woman2", "woman3")

internal object NpcEdits : NpcEditor() {
    init {
        people.forEach(::person)
    }

    private fun person(internal: String) {
        edit(internal) { contentGroup = content.person }
    }
}
