package org.rsmod.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.AbstractModule
import com.google.inject.Scopes

public object JsonModule : AbstractModule() {

    override fun configure() {
        bind(ObjectMapper::class.java)
            .annotatedWith(Json::class.java)
            .toProvider(ObjectMapperProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
