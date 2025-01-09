package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvType

public typealias invs = BaseInvs

public object BaseInvs : InvReferences() {
    public val inv: InvType = find("inv", 850981630)
    public val worn: InvType = find("worn", 847803897)
    public val bank: InvType = find("bank", 1135478129)

    public val generalshop1: InvType = find("generalshop1", 62547837000)
}
