package org.rsmod.content.interfaces.gameframe.script

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.output.ClientScripts
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.ui.ifOpenOverlay
import org.rsmod.api.player.ui.ifOpenTop
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.script.advanced.onIfMoveSub
import org.rsmod.api.script.advanced.onIfMoveTop
import org.rsmod.api.script.onPlayerInit
import org.rsmod.api.script.onPlayerSoftQueueWithArgs
import org.rsmod.content.interfaces.gameframe.Gameframe
import org.rsmod.content.interfaces.gameframe.GameframeLoader
import org.rsmod.content.interfaces.gameframe.changeGameframe
import org.rsmod.content.interfaces.gameframe.config.gameframe_queues
import org.rsmod.content.interfaces.gameframe.openGameframe
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class GameframeScript
@Inject
internal constructor(
    private val eventBus: EventBus,
    private val loader: GameframeLoader,
    private val interfaceTypes: InterfaceTypeList,
) : PluginScript() {
    private lateinit var gameframes: Map<Int, Gameframe>
    private lateinit var moveEvents: List<MoveEvent>
    private lateinit var default: Gameframe

    private var Player.gameframeTopLevel by intVarBit(varbits.gameframe_toplevel)

    override fun ScriptContext.startup() {
        loadAll()

        onPlayerInit { player.openLoginGameframe() }

        for ((topLevel, gameframe) in gameframes) {
            val type = interfaceTypes.getValue(topLevel)
            onIfMoveTop(type) { player.queueGameframeMove(gameframe) }
        }

        for ((target, event) in moveEvents) {
            onIfMoveSub(target) { player.moveSetEvents(event) }
        }
        onIfMoveSub(components.toplevel_target_xp_drops) { player.moveXpDrops() }
        onIfMoveSub(components.toplevel_target_ehc_listener) { player.moveEhcListener() }

        onPlayerSoftQueueWithArgs(gameframe_queues.client_mode) {
            player.changeGameframe(args, eventBus)
        }
    }

    private fun loadAll() {
        gameframes = loader.loadGameframes()
        moveEvents = loader.loadMoveEvents().mapMoveEvents()
        default = selectDefault(gameframes.values)
    }

    private fun Player.openLoginGameframe() {
        val gameframe = gameframes[gameframeTopLevel]
        if (gameframe != null && gameframe.resizable == ui.frameResizable) {
            ifOpenTop(gameframe.topLevel)
            openGameframe(gameframe, eventBus)
            return
        }
        val fallback = selectFallback(ui.frameResizable) ?: default
        ui.frameResizable = fallback.resizable
        gameframeTopLevel = fallback.topLevel.id
        ifOpenTop(fallback.topLevel)
        openGameframe(fallback, eventBus)
    }

    private fun Player.queueGameframeMove(gameframe: Gameframe) {
        if (gameframeTopLevel == gameframe.topLevel.id) {
            return
        }
        val settingsClientMode = ui.frameResizable != gameframe.resizable
        if (settingsClientMode) {
            runClientScript(3998, gameframe.clientMode)
        }

        ui.frameResizable = gameframe.resizable
        gameframeTopLevel = gameframe.topLevel.id

        val queueDelay = if (settingsClientMode) 2 else 1
        softQueue(gameframe_queues.client_mode, queueDelay, gameframe)
    }

    private fun Player.moveSetEvents(component: ComponentType) {
        ifSetEvents(component, -1..-1, IfEvent.Op1)
    }

    private fun Player.moveXpDrops() {
        ifOpenOverlay(interfaces.orbs, components.toplevel_target_orbs, eventBus)
    }

    private fun Player.moveEhcListener() {
        ClientScripts.settingsInterfaceScaling(this, 0)
        ClientScripts.buffBarLayoutRedraw(this)
        ifOpenOverlay(interfaces.orbs, components.toplevel_target_orbs, eventBus)
    }

    private fun Map<ComponentType, ComponentType>.mapMoveEvents(): List<MoveEvent> {
        return map { MoveEvent(it.key, it.value) }
    }

    private fun selectDefault(from: Iterable<Gameframe>): Gameframe {
        return from.single(Gameframe::isDefault)
    }

    private fun selectFallback(resizable: Boolean): Gameframe? {
        return gameframes.values.firstOrNull { it.resizable == resizable }
    }

    private data class MoveEvent(val target: ComponentType, val event: ComponentType)
}
