package org.rsmod.api.config.editors

import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor

public object ObjEdits : ObjEditor() {
    init {
        edit("coins") { param[params.shop_sale_restricted] = true }
        edit("platinum_token") { param[params.shop_sale_restricted] = true }
    }
}
