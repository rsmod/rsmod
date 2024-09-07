package org.rsmod.api.shops

import org.rsmod.plugin.module.PluginModule

public class ShopsModule : PluginModule() {
    override fun bind() {
        bindInstance<Shops>()
    }
}
