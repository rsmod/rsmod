package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.api.type.refs.param.ParamReferences
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varbit.VarBitType

internal typealias prayer_params = PrayerTabParams

object PrayerTabParams : ParamReferences() {
    val id = find<Int>("prayer_id", 88673346945)
    val component = find<ComponentType>("prayer_component", 61646266434)
    val name = find<String>("prayer_name", 9222941699801316537)
    val level = find<Int>("prayer_levelreq", 29733805813656036)
    val sound = find<SynthType>("prayer_sound", 67558440545)
    val varbit = find<VarBitType>("prayer_varbit")
    val overhead = find<Int>("prayer_overhead")
    val unlock_varbit = find<VarBitType>("prayer_unlock_varbit")
    val unlock_state = find<Int>("prayer_unlock_state")
    val locked_message = find<String>("prayer_locked_message")
}

internal object PrayerTabParamBuilder : ParamBuilder() {
    init {
        build<Int>("prayer_overhead")
        build<VarBitType>("prayer_varbit")
        build<VarBitType>("prayer_unlock_varbit")
        build<Int>("prayer_unlock_state") { default = 1 }
        build<String>("prayer_locked_message")
    }
}
