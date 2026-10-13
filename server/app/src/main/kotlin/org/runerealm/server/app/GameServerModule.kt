package org.runerealm.server.app

import org.runerealm.module.ExtendedModule
import org.runerealm.server.app.modules.GameModule
import org.runerealm.server.app.modules.ParserModule
import org.runerealm.server.app.modules.ServiceModule
import org.runerealm.server.shared.module.CacheStoreModule
import org.runerealm.server.shared.module.ScannerModule
import org.runerealm.server.shared.module.SymbolModule

object GameServerModule : ExtendedModule() {
    override fun bind() {
        install(CacheStoreModule)
        install(GameModule)
        install(ParserModule)
        install(ScannerModule)
        install(ServiceModule)
        install(SymbolModule)
    }
}
