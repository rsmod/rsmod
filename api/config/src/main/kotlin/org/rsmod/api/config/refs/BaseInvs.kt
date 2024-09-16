package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvType

public typealias invs = BaseInvs

public object BaseInvs : InvReferences() {
    public val inv: InvType = find(850981630)
    public val worn: InvType = find(847803897)
    public val bank: InvType = find(1135478129)

    public val generalshop1: InvType = find(62547837000)
}
