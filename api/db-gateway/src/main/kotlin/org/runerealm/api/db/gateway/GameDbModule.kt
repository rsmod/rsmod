package org.runerealm.api.db.gateway

import org.runerealm.api.db.gateway.service.ResponseDbGatewayService
import org.runerealm.plugin.module.PluginModule
import org.runerealm.server.services.Service

public class GameDbModule : PluginModule() {
    override fun bind() {
        bindInstance<GameDbManager>()
        bindInstance<GameDbSynchronizer>()
        bindInstance<ResponseDbGatewayService>()
        addSetBinding<Service>(ResponseDbGatewayService::class.java)
    }
}
