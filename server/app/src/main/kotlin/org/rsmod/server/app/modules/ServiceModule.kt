package org.rsmod.server.app.modules

import org.rsmod.module.ExtendedModule
import org.rsmod.server.app.GameService
import org.rsmod.server.services.Service

object ServiceModule : ExtendedModule() {
    override fun bind() {
        addSetBinding<Service>(GameService::class.java)
    }
}
