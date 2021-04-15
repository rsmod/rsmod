package org.rsmod.game

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.game.action.ActionBus
import org.rsmod.game.cmd.CommandMap
import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.event.EventBus
import org.rsmod.game.model.client.ClientList
import org.rsmod.game.model.map.MapIsolation
import org.rsmod.game.model.mob.NpcList
import org.rsmod.game.model.mob.PlayerList
import org.rsmod.game.model.obj.GameObjectApSet
import org.rsmod.game.model.obj.GameObjectMap
import org.rsmod.game.model.world.World
import org.rsmod.game.privilege.PrivilegeMap
import org.rsmod.game.task.StartupTaskList
import org.rsmod.game.update.task.UpdateTaskList

class GameModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<Game>().`in`(scope)
        bind<World>().`in`(scope)
        bind<EventBus>().`in`(scope)
        bind<ActionBus>().`in`(scope)
        bind<StartupTaskList>().`in`(scope)
        bind<CommandMap>().`in`(scope)
        bind<PlayerList>().`in`(scope)
        bind<ClientList>().`in`(scope)
        bind<NpcList>().`in`(scope)
        bind<UpdateTaskList>().`in`(scope)
        bind<MapIsolation>().`in`(scope)
        bind<CollisionMap>().`in`(scope)
        bind<GameObjectMap>().`in`(scope)
        bind<GameObjectApSet>().`in`(scope)
        bind<PrivilegeMap>().`in`(scope)
    }
}
