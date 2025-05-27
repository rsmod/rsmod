package org.rsmod.content.interfaces.settings.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias setting_varbits = SettingVarBits

object SettingVarBits : VarBitReferences() {
    val panel_tab = find("settings_side_panel_tab", 92479944086622)
}
