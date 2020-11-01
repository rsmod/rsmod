package org.rsmod.plugins.content.serializer.yaml

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.game.model.domain.serializer.ClientSerializer

class YamlSerializerModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<ClientSerializer>()
            .to<YamlClientSerializer<*>>()
            .`in`(scope)
    }
}
