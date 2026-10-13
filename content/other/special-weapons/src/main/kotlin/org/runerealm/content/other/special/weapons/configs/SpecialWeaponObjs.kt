package org.runerealm.content.other.special.weapons.configs

import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.params
import org.runerealm.api.type.editors.obj.ObjEditor

internal object SpecialWeaponObjEditor : ObjEditor() {
    init {
        edit(objs.tumekens_shadow) {
            param[params.uncharged_variant] = objs.tumekens_shadow_uncharged
        }
        edit(objs.tumekens_shadow_uncharged) {
            param[params.charged_variant] = objs.tumekens_shadow
        }
    }
}
