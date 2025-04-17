package org.rsmod.content.generic.npcs.ducks

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

internal typealias duck_npcs = DuckNpcs

internal object DuckNpcs : NpcReferences() {
    val duck = find("duck")
    val duck_female = find("duck_female")
    val duckling = find("duck_update_ducklings")
}

internal object DuckNpcEdits : NpcEditor() {
    init {
        val ducks = setOf(duck_npcs.duck, duck_npcs.duck_female)
        ducks.forEach(::duck)

        val ducklings = setOf(duck_npcs.duckling)
        ducklings.forEach(::duckling)
    }

    private fun duck(type: NpcType) {
        edit(type) {
            moveRestrict = blocked
            contentGroup = content.duck
            timer = 1
        }
    }

    private fun duckling(type: NpcType) {
        edit(type) {
            moveRestrict = blocked
            contentGroup = content.duckling
            timer = 1
        }
    }
}
