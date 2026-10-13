package org.runerealm.api.obj.charges

import org.runerealm.plugin.module.PluginModule

internal class ObjChargeModule : PluginModule() {
    override fun bind() {
        bindInstance<ObjChargeManager>()
    }
}
