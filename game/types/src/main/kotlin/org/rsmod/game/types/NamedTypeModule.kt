package org.rsmod.game.types

import com.google.inject.AbstractModule
import com.google.inject.Scopes

public object NamedTypeModule : AbstractModule() {

    override fun configure() {
        bind(NamedTypes::class.java).`in`(Scopes.SINGLETON)
    }
}
