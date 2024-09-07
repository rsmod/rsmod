package org.rsmod.api.parsers.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder

public object JsonModule : AbstractModule() {
    override fun configure() {
        bind(ObjectMapper::class.java)
            .annotatedWith(Json::class.java)
            .toProvider(ObjectMapperProvider::class.java)
            .`in`(Scopes.SINGLETON)
        Multibinder.newSetBinder(binder(), Module::class.java)
    }
}
