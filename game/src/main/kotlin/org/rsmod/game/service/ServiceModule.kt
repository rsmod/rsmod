package org.rsmod.game.service

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import org.rsmod.game.GameService
import org.rsmod.game.net.NetworkService

public object ServiceModule : AbstractModule() {

    override fun configure() {
        val binder = Multibinder.newSetBinder(binder(), Service::class.java)
        binder.addBinding().to(GameService::class.java)
        binder.addBinding().to(NetworkService::class.java)
    }
}
