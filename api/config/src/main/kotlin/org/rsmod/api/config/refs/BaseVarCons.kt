package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.varcon.VarConBuilder
import org.rsmod.game.type.varcon.VarConType

public typealias varcons = BaseVarCons

public object BaseVarCons : VarConBuilder() {
    public val woodcutting_tree_cut_ticks: VarConType = build("woodcutting_tree_cut_ticks")
    public val woodcutting_tree_last_cut: VarConType = build("woodcutting_tree_last_cut")
    public val woodcutting_tree_loc: VarConType = build("woodcutting_tree_loc")
}
