package org.runerealm.api.pw.hash

import org.runerealm.api.pw.hash.argon2.Argon2PasswordHashing
import org.runerealm.module.ExtendedModule

public object PasswordHashModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<PasswordHashing>(Argon2PasswordHashing::class.java)
    }
}
