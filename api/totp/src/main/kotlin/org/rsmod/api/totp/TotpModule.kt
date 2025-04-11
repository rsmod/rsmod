package org.rsmod.api.totp

import org.rsmod.api.totp.google.GoogleTotpManager
import org.rsmod.module.ExtendedModule

public object TotpModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<TotpManager>(GoogleTotpManager::class.java)
    }
}
