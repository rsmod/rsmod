package org.runerealm.api.type.updater

import org.runerealm.module.ExtendedModule

public object TypeUpdaterModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeUpdaterCacheSync>()
        bindInstance<TypeUpdaterConfigs>()
        bindInstance<TypeUpdaterResources>()
    }
}
