package org.runerealm.api.totp

import org.runerealm.api.totp.google.GoogleTotp
import org.runerealm.module.ExtendedModule

public object TotpModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<Totp>(GoogleTotp::class.java)
    }
}
