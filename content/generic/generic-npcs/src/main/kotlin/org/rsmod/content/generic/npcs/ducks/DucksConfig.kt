package org.rsmod.content.generic.npcs.ducks

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor

private val ducks = setOf("duck", "duck_female")
private val ducklings = setOf("duck_update_ducklings")

internal object NpcEdits : NpcEditor() {
    init {
        ducks.forEach(::duck)
        ducklings.forEach(::duckling)
    }

    private fun duck(internal: String) {
        edit(internal) {
            moveRestrict = blocked
            contentGroup = content.duck
            timer = 1
        }
    }

    private fun duckling(internal: String) {
        edit(internal) {
            moveRestrict = blocked
            contentGroup = content.duckling
            timer = 1
        }
    }
}
