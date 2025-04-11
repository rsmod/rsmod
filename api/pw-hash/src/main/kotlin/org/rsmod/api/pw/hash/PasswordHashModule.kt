package org.rsmod.api.pw.hash

import org.rsmod.api.pw.hash.argon2.Argon2PasswordHashing
import org.rsmod.module.ExtendedModule

public object PasswordHashModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<PasswordHashing>(Argon2PasswordHashing::class.java)
    }
}
