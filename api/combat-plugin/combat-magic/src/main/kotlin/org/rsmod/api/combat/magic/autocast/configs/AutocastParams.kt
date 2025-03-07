package org.rsmod.api.combat.magic.autocast.configs

import org.rsmod.api.config.aliases.ParamObj
import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.api.type.refs.param.ParamReferences
import org.rsmod.game.type.obj.ObjType

internal typealias autocast_params = AutocastParams

internal object AutocastParams : ParamReferences() {
    val additional_spell_autocast1: ParamObj = find("additional_spell_autocast1")
    val additional_spell_autocast2: ParamObj = find("additional_spell_autocast2")
    val additional_spell_autocast3: ParamObj = find("additional_spell_autocast3")
}

internal object AutocastParamBuilder : ParamBuilder() {
    init {
        build<ObjType>("additional_spell_autocast1")
        build<ObjType>("additional_spell_autocast2")
        build<ObjType>("additional_spell_autocast3")
    }
}
