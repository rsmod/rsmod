package org.rsmod.content.generic.npcs.ducks

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.builders.hunt.HuntModeBuilder
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.hunt.HuntModeReferences
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.type.hunt.HuntType
import org.rsmod.game.type.hunt.HuntVis
import org.rsmod.game.type.npc.NpcType

typealias duck_npcs = DuckNpcs

typealias duck_hunt = DuckHunt

object DuckNpcs : NpcReferences() {
    val duck = find("duck")
    val duck_female = find("duck_female")
    val duckling = find("duck_update_ducklings")
}

object DuckNpcEdits : NpcEditor() {
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
            timer = 25
            wanderRange = 35
            maxRange = 40
        }
    }

    private fun duckling(type: NpcType) {
        edit(type) {
            defaultMode = none
            moveRestrict = blocked
            contentGroup = content.duckling
            timer = 50
            wanderRange = 35
            maxRange = 40
            huntMode = duck_hunt.duckling
            huntRange = 2
        }
    }
}

object DuckHunt : HuntModeReferences() {
    val duckling = find("duck_hunt")
}

object DuckHuntBuilder : HuntModeBuilder() {
    init {
        build("duck_hunt") {
            type = HuntType.Npc
            checkNpc { npc = duck_npcs.duck_female }
            checkVis = HuntVis.LineOfSight
            findKeepHunting = true
            rate = 11
            findNewMode = NpcMode.OpNpc3
        }
    }
}
