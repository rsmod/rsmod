package org.rsmod.util.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule

class ObjectMapperModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<ObjectMapper>()
            .toProvider<ObjectMapperProvider>()
            .`in`(scope)
    }
}
