package org.rsmod.json

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder

public object JsonModule : AbstractModule() {

    override fun configure() {
        /* bind jackson module set */
        Multibinder.newSetBinder(binder(), Module::class.java)

        bind(ObjectMapper::class.java)
            .annotatedWith(Json::class.java)
            .toProvider(ObjectMapperProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
