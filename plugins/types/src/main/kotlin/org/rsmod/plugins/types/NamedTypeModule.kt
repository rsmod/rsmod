package org.rsmod.plugins.types

import com.google.inject.AbstractModule
import com.google.inject.Scopes

public object NamedTypeModule : AbstractModule() {

    override fun configure() {
        bind(NamedTypeMapHolder::class.java).`in`(Scopes.SINGLETON)
    }
}
