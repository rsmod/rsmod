package org.rsmod.game.client

import com.google.inject.AbstractModule
import com.google.inject.Scopes

public object ClientModule : AbstractModule() {

    override fun configure() {
        bind(ClientList::class.java).`in`(Scopes.SINGLETON)
    }
}
