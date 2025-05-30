@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.inv.InvReferences

typealias invs = BaseInvs

object BaseInvs : InvReferences() {
    val tradeoffer = find("tradeoffer", 850951859)
    val inv = find("inv", 850981630)
    val worn = find("worn", 847803897)
    val bank = find("bank", 1135478129)

    val generalshop1 = find("generalshop1", 62547837000)
}
