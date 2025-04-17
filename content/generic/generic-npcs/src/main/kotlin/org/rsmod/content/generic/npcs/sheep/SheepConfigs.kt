package org.rsmod.content.generic.npcs.sheep

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

internal object SheepNpcEditor : NpcEditor() {
    init {
        sheep(sheep_npcs.unsheared_2693, sheep_npcs.sheared_1178)
        sheep(sheep_npcs.unsheared_2696, sheep_npcs.sheared_1301)
        sheep(sheep_npcs.unsheared_2699, sheep_npcs.sheared_1304)
        sheep(sheep_npcs.unsheared_2694, sheep_npcs.sheared_1299)
        sheep(sheep_npcs.unsheared_2697, sheep_npcs.sheared_1302)
        sheep(sheep_npcs.unsheared_2786, sheep_npcs.sheared_1308)
        sheep(sheep_npcs.unsheared_2695, sheep_npcs.sheared_1300)
        sheep(sheep_npcs.unsheared_2698, sheep_npcs.sheared_1303)
        sheep(sheep_npcs.unsheared_2787, sheep_npcs.sheared_1309)
        sheep(sheep_npcs.unsheared_2788, sheep_npcs.sheared_2691)
        sheep(sheep_npcs.unsheared_2789, sheep_npcs.sheared_2692)
        sheep(sheep_npcs.unsheared_5843, sheep_npcs.sheared_5845)
        sheep(sheep_npcs.unsheared_5844, sheep_npcs.sheared_5846)
    }

    private fun sheep(unsheared: NpcType, sheared: NpcType) {
        edit(unsheared.internalNameValue) {
            contentGroup = content.sheep
            param[params.next_npc_stage] = sheared
            timer = 1
        }
        edit(sheared.internalNameValue) {
            contentGroup = content.sheared_sheep
            timer = 1
        }
    }
}

internal typealias sheep_npcs = SheepNpcs

object SheepNpcs : NpcReferences() {
    val unsheared_2693 = find("sheepunsheered")
    val unsheared_2696 = find("sheepunsheered2")
    val unsheared_2699 = find("sheepunsheered3")
    val unsheared_2694 = find("sheepunsheeredg")
    val unsheared_2697 = find("sheepunsheered2g")
    val unsheared_2786 = find("sheepunsheered3g")
    val unsheared_2695 = find("sheepunsheeredw")
    val unsheared_2698 = find("sheepunsheered2w")
    val unsheared_2787 = find("sheepunsheered3w")
    val unsheared_2788 = find("sheepunsheeredshaggy")
    val unsheared_2789 = find("sheepunsheeredshaggy2")
    val unsheared_5843 = find("fairy_sheep")
    val unsheared_5844 = find("fairy_sheep2")

    val sheared_1178 = find("sheepsheered")
    val sheared_1301 = find("sheepsheered2")
    val sheared_1304 = find("sheepsheered3")
    val sheared_1299 = find("sheepsheeredg")
    val sheared_1302 = find("sheepsheered2g")
    val sheared_1308 = find("sheepsheered3g")
    val sheared_1300 = find("sheepsheeredw")
    val sheared_1303 = find("sheepsheered2w")
    val sheared_1309 = find("sheepsheered3w")
    val sheared_2691 = find("sheepsheeredshaggy")
    val sheared_2692 = find("sheepsheeredshaggy2")
    val sheared_5845 = find("fairy_sheepsheered")
    val sheared_5846 = find("fairy_sheepsheered2")
}
