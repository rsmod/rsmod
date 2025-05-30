package org.rsmod.content.travel.spirittree.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias spirit_tree_locs = SpiritTreeLocs

object SpiritTreeLocs : LocReferences() {
    val spirittree_big_1op = find("spirittree_big_1op")
    val spirittree_big_2ops = find("spirittree_big_2ops")
    val spirittree_big_2ops_orbs = find("spirittree_big_2ops_orbs")
    val spirittree_small_1op = find("spirittree_small_1op")
    val spirittree_small_2ops = find("spirittree_small_2ops")
    val spirittree_prif_1op = find("spirittree_prif_1op")
    val spirittree_prif_2ops = find("spirittree_prif_2ops")
    val spirittree_prif = find("spirittree_prif")
}

object SpiritTree : LocEditor() {
    init {
        edit(spirit_tree_locs.spirittree_big_1op) {
            contentGroup = content.spirit_tree
            op1 = "Talk-to"
            op2 = "Travel"
        }
        edit(spirit_tree_locs.spirittree_big_2ops) {
            contentGroup = content.spirit_tree
            op2 = "Travel"
            op1 = "Talk-to"
        }
        edit(spirit_tree_locs.spirittree_big_2ops_orbs) {
            contentGroup = content.spirit_tree
            op2 = "Travel"
            op1 = "Talk-to"
        }
        edit(spirit_tree_locs.spirittree_small_1op) {
            contentGroup = content.spirit_tree
            op2 = "Travel"
            op1 = "Talk-to"
        }
        edit(spirit_tree_locs.spirittree_small_2ops) {
            contentGroup = content.spirit_tree
            op2 = "Travel"
            op1 = "Talk-to"
        }
        edit(spirit_tree_locs.spirittree_prif_1op) {
            contentGroup = content.spirit_tree
        }
        edit(spirit_tree_locs.spirittree_prif_2ops) {
            contentGroup = content.spirit_tree
        }
        edit(spirit_tree_locs.spirittree_prif) {
            contentGroup = content.spirit_tree
        }
    }
}
