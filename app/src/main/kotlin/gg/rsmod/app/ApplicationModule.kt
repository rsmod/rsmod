package gg.rsmod.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.util.config.ObjectMapperProvider
import gg.rsmod.util.security.BCryptEncryption
import gg.rsmod.util.security.PasswordEncryption

class ApplicationModule(
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
