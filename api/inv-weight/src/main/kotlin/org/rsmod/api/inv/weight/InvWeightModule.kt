package org.rsmod.api.inv.weight

import org.rsmod.plugin.module.PluginModule

public class InvWeightModule : PluginModule() {
    override fun bind() {
        bindInstance<InvWeight>()
    }
}
