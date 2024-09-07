package org.rsmod.api.parsers.toml

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder

public object TomlModule : AbstractModule() {
    override fun configure() {
        bind(ObjectMapper::class.java)
            .annotatedWith(Toml::class.java)
            .toProvider(ObjectMapperProvider::class.java)
            .`in`(Scopes.SINGLETON)
        Multibinder.newSetBinder(binder(), Module::class.java)
    }
}
