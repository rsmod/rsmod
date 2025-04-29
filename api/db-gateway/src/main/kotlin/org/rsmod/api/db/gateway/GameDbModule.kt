package org.rsmod.api.db.gateway

import org.rsmod.api.db.gateway.service.ResponseDbGatewayService
import org.rsmod.plugin.module.PluginModule
import org.rsmod.server.services.Service

public class GameDbModule : PluginModule() {
    override fun bind() {
        bindInstance<GameDbManager>()
        bindInstance<GameDbSynchronizer>()
        bindInstance<ResponseDbGatewayService>()
        addSetBinding<Service>(ResponseDbGatewayService::class.java)
    }
}
