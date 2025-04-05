package org.rsmod.api.obj.charges

import org.rsmod.plugin.module.PluginModule

internal class ObjChargeModule : PluginModule() {
    override fun bind() {
        bindInstance<ObjChargeManager>()
    }
}
