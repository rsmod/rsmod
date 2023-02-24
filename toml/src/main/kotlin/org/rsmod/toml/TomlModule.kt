package org.rsmod.toml

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder

public object TomlModule : AbstractModule() {

    override fun configure() {
        /* bind jackson module set */
        Multibinder.newSetBinder(binder(), Module::class.java)

        bind(ObjectMapper::class.java)
            .annotatedWith(Toml::class.java)
            .toProvider(ObjectMapperProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
