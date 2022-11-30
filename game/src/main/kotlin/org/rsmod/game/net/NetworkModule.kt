package org.rsmod.game.net

import com.google.inject.AbstractModule
import com.google.inject.Scope

public class NetworkModule(private val scope: Scope) : AbstractModule() {

    override fun configure() {
        bind(NetworkBootstrapFactory::class.java).`in`(scope)
        bind(NetworkChannelInitializer::class.java).`in`(scope)
    }
}
