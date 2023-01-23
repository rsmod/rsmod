package org.rsmod.toml

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.AbstractModule
import com.google.inject.Scopes

public object TomlModule : AbstractModule() {

    override fun configure() {
        bind(ObjectMapper::class.java)
            .annotatedWith(Toml::class.java)
            .toProvider(ObjectMapperProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
