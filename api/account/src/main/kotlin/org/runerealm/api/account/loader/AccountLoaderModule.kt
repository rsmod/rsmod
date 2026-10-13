package org.runerealm.api.account.loader

import org.runerealm.module.ExtendedModule
import org.runerealm.server.services.Service

public object AccountLoaderModule : ExtendedModule() {
    override fun bind() {
        bindInstance<AccountLoaderService>()
        addSetBinding<Service>(AccountLoaderService::class.java)
    }
}
