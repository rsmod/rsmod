package org.rsmod.api.hunt

import org.rsmod.module.ExtendedModule

public object HuntModule : ExtendedModule() {
    override fun bind() {
        bindInstance<Hunt>()
        bindInstance<NpcSearch>()
        bindInstance<PlayerSearch>()
    }
}
