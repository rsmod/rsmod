package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.game.type.varbit.VarBitType

public typealias varbits = BaseVarBits

public object BaseVarBits : VarBitReferences() {
    public val chatbox_unlocked: VarBitType = find("chatbox_unlocked", 394391358)
    public val modal_widthandheight_mode: VarBitType = find("modal_widthandheight_mode", 231792309)
    public val hide_roofs: VarBitType = find("hide_roofs", 697869214)
    public val rt7_enabled: VarBitType = find("rt7_enabled", 861505757)
    public val rt7_mode: VarBitType = find("rt7_mode", 861509540)
    public val rt7_enabled2: VarBitType = find("rt7_enabled2", 861513323)

    public val demon_slayer_progress: VarBitType = find("demon_slayer_progress", 50392587)
}
