package org.runerealm.api.account.saver

import org.runerealm.module.ExtendedModule
import org.runerealm.server.services.Service

public object AccountSavingModule : ExtendedModule() {
    override fun bind() {
        bindInstance<AccountSavingService>()
        addSetBinding<Service>(AccountSavingService::class.java)
    }
}
