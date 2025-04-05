package org.rsmod.content.other.special.weapons.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor

internal object SpecialWeaponObjEditor : ObjEditor() {
    init {
        edit("tumekens_shadow") { param[params.uncharged_variant] = objs.tumekens_shadow_uncharged }
        edit("tumekens_shadow_uncharged") { param[params.charged_variant] = objs.tumekens_shadow }
    }
}
