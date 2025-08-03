package org.rsmod.api.type.updater

import org.rsmod.module.ExtendedModule

public object TypeUpdaterModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeUpdaterCacheSync>()
        bindInstance<TypeUpdaterConfigs>()
        bindInstance<TypeUpdaterResources>()
    }
}
