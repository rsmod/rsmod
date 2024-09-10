package org.rsmod.server.app.modules

import org.rsmod.api.core.CoreModule
import org.rsmod.api.game.process.MainGameProcess
import org.rsmod.events.KeyedEventBus
import org.rsmod.events.SuspendEventBus
import org.rsmod.events.UnboundEventBus
import org.rsmod.game.GameProcess
import org.rsmod.game.MapClock
import org.rsmod.game.cheat.CheatCommandMap
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.PlayerList
import org.rsmod.module.ExtendedModule
import org.rsmod.server.shared.module.EventModule

object GameModule : ExtendedModule() {
    override fun bind() {
        install(CoreModule)
        install(EventModule)
        bindInstance<MapClock>()
        bindInstance<PlayerList>()
        bindInstance<NpcList>()
        bindInstance<KeyedEventBus>()
        bindInstance<SuspendEventBus>()
        bindInstance<UnboundEventBus>()
        bindInstance<CheatCommandMap>()
        bindBaseInstance<GameProcess, MainGameProcess>()
    }
}