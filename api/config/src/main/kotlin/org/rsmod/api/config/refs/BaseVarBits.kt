package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.game.type.varbit.VarBitType

public typealias varbits = BaseVarBits

public object BaseVarBits : VarBitReferences() {
    public val chatbox_unlocked: VarBitType = find(394391358)
    public val modal_widthandheight_mode: VarBitType = find(231792309)
    public val hide_roofs: VarBitType = find(697869214)
    public val rt7_enabled: VarBitType = find(861505757)
    public val rt7_mode: VarBitType = find(861509540)
    public val rt7_enabled2: VarBitType = find(861513323)

    public val demon_slayer_progress: VarBitType = find(50392587)
}
