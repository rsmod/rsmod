package org.rsmod.api.multiway

import org.rsmod.module.ExtendedModule

public object MultiwayModule : ExtendedModule() {
    override fun bind() {
        bindInstance<MultiwayChecker>()
    }
}
