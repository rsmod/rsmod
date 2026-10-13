package org.runerealm.server.app.modules

import org.runerealm.api.core.CoreModule
import org.runerealm.api.game.process.MainGameProcess
import org.runerealm.events.KeyedEventMap
import org.runerealm.events.SuspendEventMap
import org.runerealm.events.UnboundEventMap
import org.runerealm.game.GameProcess
import org.runerealm.game.GameUpdate
import org.runerealm.game.MapClock
import org.runerealm.game.cheat.CheatCommandMap
import org.runerealm.game.entity.ControllerList
import org.runerealm.game.entity.NpcList
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.queue.EngineQueueCache
import org.runerealm.game.region.RegionListLarge
import org.runerealm.game.region.RegionListSmall
import org.runerealm.module.ExtendedModule
import org.runerealm.server.shared.module.EventModule

object GameModule : ExtendedModule() {
    override fun bind() {
        install(CoreModule)
        install(EventModule)
        bindInstance<GameUpdate>()
        bindInstance<MapClock>()
        bindInstance<NpcList>()
        bindInstance<PlayerList>()
        bindInstance<ControllerList>()
        bindInstance<RegionListSmall>()
        bindInstance<RegionListLarge>()
        bindInstance<KeyedEventMap>()
        bindInstance<SuspendEventMap>()
        bindInstance<UnboundEventMap>()
        bindInstance<CheatCommandMap>()
        bindInstance<EngineQueueCache>()
        bindBaseInstance<GameProcess>(MainGameProcess::class.java)
    }
}
