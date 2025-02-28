package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.varn.VarnReferences

typealias varns = BaseVarns

object BaseVarns : VarnReferences() {
    val lastcombat = find("lastcombat")
    val aggressive_player = find("aggressive_player")
}
