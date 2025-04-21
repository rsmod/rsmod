package org.rsmod.content.interfaces.settings.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias setting_components = SettingComponents

object SettingComponents : ComponentReferences() {
    val runbutton_orb = find("orbs:runbutton")
    val runmode = find("settings_side:runmode")
}
