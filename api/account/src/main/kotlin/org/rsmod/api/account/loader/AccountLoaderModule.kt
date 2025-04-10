package org.rsmod.api.account.loader

import org.rsmod.module.ExtendedModule
import org.rsmod.server.services.Service

public object AccountLoaderModule : ExtendedModule() {
    override fun bind() {
        bindInstance<AccountLoaderService>()
        addSetBinding<Service>(AccountLoaderService::class.java)
    }
}
