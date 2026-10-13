package org.runerealm.api.spells.autocast.configs

import org.runerealm.api.config.aliases.ParamObj
import org.runerealm.api.type.builders.param.ParamBuilder
import org.runerealm.api.type.refs.param.ParamReferences
import org.runerealm.game.type.obj.ObjType

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
