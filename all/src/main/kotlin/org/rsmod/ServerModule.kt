package org.rsmod

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.util.security.BCryptEncryption
import org.rsmod.util.security.PasswordEncryption

class ServerModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<PasswordEncryption>()
            .to<BCryptEncryption>()
            .`in`(scope)
    }
}
