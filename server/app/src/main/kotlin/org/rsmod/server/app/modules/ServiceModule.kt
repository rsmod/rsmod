package org.rsmod.server.app.modules

import com.google.common.util.concurrent.Service
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import org.rsmod.module.ExtendedModule
import org.rsmod.server.app.GameService

object ServiceModule : ExtendedModule() {
    override fun bind() {
        Multibinder.newSetBinder(binder(), Service::class.java)
            .addBinding()
            .to(GameService::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
