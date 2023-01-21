package org.rsmod.game.net

import com.google.inject.AbstractModule

public object NetworkModule : AbstractModule() {

    override fun configure() {
        bind(NetworkBootstrapFactory::class.java)
        bind(NetworkChannelInitializer::class.java)
    }
}
