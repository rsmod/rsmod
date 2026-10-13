package org.runerealm.server.app.modules

import org.runerealm.module.ExtendedModule
import org.runerealm.server.app.GameService
import org.runerealm.server.services.Service

object ServiceModule : ExtendedModule() {
    override fun bind() {
        addSetBinding<Service>(GameService::class.java)
    }
}
