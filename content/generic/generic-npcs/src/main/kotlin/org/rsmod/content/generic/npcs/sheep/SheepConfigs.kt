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
    val unsheared_2693 = find("sheep_id_2693")
    val unsheared_2696 = find("sheep_id_2696")
    val unsheared_2699 = find("sheep_id_2699")
    val unsheared_2694 = find("sheep_id_2694")
    val unsheared_2697 = find("sheep_id_2697")
    val unsheared_2786 = find("sheep_id_2786")
    val unsheared_2695 = find("sheep_id_2695")
    val unsheared_2698 = find("sheep_id_2698")
    val unsheared_2787 = find("sheep_id_2787")
    val unsheared_2788 = find("sheep_id_2788")
    val unsheared_2789 = find("sheep_id_2789")
    val unsheared_5843 = find("sheep_id_5843")
    val unsheared_5844 = find("sheep_id_5844")

    val sheared_1178 = find("sheep_id_1178")
    val sheared_1301 = find("sheep_id_1301")
    val sheared_1304 = find("sheep_id_1304")
    val sheared_1299 = find("sheep_id_1299")
    val sheared_1302 = find("sheep_id_1302")
    val sheared_1308 = find("sheep_id_1308")
    val sheared_1300 = find("sheep_id_1300")
    val sheared_1303 = find("sheep_id_1303")
    val sheared_1309 = find("sheep_id_1309")
    val sheared_2691 = find("sheep_id_2691")
    val sheared_2692 = find("sheep_id_2692")
    val sheared_5845 = find("sheep_id_5845")
    val sheared_5846 = find("sheep_id_5846")
}
