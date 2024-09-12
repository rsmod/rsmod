package org.rsmod.api.core.module

import org.rsmod.api.player.protect.ProtectedAccessContextFactory
import org.rsmod.module.ExtendedModule

public object PlayerModule : ExtendedModule() {
    override fun bind() {
        bindInstance<ProtectedAccessContextFactory>()
    }
}
