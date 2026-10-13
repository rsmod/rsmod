package org.runerealm.api.hunt

import org.runerealm.module.ExtendedModule

public object HuntModule : ExtendedModule() {
    override fun bind() {
        bindInstance<Hunt>()
        bindInstance<NpcSearch>()
        bindInstance<PlayerSearch>()
    }
}
