package org.rsmod.api.totp

import org.rsmod.api.totp.google.GoogleTotp
import org.rsmod.module.ExtendedModule

public object TotpModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<Totp>(GoogleTotp::class.java)
    }
}
