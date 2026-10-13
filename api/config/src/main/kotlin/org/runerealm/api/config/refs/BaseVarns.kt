package org.runerealm.api.config.refs

import org.runerealm.api.type.refs.varn.VarnReferences

typealias varns = BaseVarns

object BaseVarns : VarnReferences() {
    val lastcombat = find("lastcombat")
    val aggressive_player = find("aggressive_player")
    val generic_state_2 = find("generic_state_2")
    val attacking_player = find("attacking_player")
    val lastattack = find("lastattack")
    val flat_armour = find("flat_armour")
    val freeze_end = find("freeze_end")
    val freeze_immunity_end = find("freeze_immunity_end")
}
