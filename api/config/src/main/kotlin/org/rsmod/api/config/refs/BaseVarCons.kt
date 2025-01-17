@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.varcon.VarConBuilder

typealias varcons = BaseVarCons

object BaseVarCons : VarConBuilder() {
    val woodcutting_tree_cut_ticks = build("woodcutting_tree_cut_ticks")
    val woodcutting_tree_last_cut = build("woodcutting_tree_last_cut")
    val woodcutting_tree_loc = build("woodcutting_tree_loc")
}
