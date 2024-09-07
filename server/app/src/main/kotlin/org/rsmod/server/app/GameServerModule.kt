package org.rsmod.server.app

import org.rsmod.module.ExtendedModule
import org.rsmod.server.app.modules.DispatcherModule
import org.rsmod.server.app.modules.GameModule
import org.rsmod.server.app.modules.ParserModule
import org.rsmod.server.app.modules.ServiceModule
import org.rsmod.server.app.modules.TaskModule
import org.rsmod.server.shared.module.CacheStoreModule
import org.rsmod.server.shared.module.ScannerModule
import org.rsmod.server.shared.module.SymbolModule

object GameServerModule : ExtendedModule() {
    override fun bind() {
        install(CacheStoreModule)
        install(DispatcherModule)
        install(GameModule)
        install(ParserModule)
        install(ScannerModule)
        install(ServiceModule)
        install(SymbolModule)
        install(TaskModule)
    }
}
