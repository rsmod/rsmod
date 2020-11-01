package org.rsmod

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.util.config.ObjectMapperProvider
import org.rsmod.util.security.BCryptEncryption
import org.rsmod.util.security.PasswordEncryption

class ServerModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<ObjectMapper>()
            .toProvider<ObjectMapperProvider>()
            .`in`(scope)

        bind<PasswordEncryption>()
            .to<BCryptEncryption>()
            .`in`(scope)
    }
}
