package org.rsmod.content.interfaces.gameframe.script

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.cinematic.Cinematic
import org.rsmod.api.player.output.ClientScripts
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.ui.ifOpenOverlay
import org.rsmod.api.player.ui.ifOpenTop
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.player.vars.resyncVar
import org.rsmod.api.script.advanced.onIfMoveSub
import org.rsmod.api.script.advanced.onIfMoveTop
import org.rsmod.api.script.onPlayerInit
import org.rsmod.api.script.onPlayerSoftQueueWithArgs
import org.rsmod.content.interfaces.gameframe.Gameframe
import org.rsmod.content.interfaces.gameframe.GameframeLoader
import org.rsmod.content.interfaces.gameframe.GameframeMove
import org.rsmod.content.interfaces.gameframe.config.gameframe_queues
import org.rsmod.content.interfaces.gameframe.moveGameframe
import org.rsmod.content.interfaces.gameframe.openGameframe
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.interf.isType
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
    private var Player.stoneArrangements by boolVarBit(varbits.resizable_stone_arrangement)

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

        onPlayerSoftQueueWithArgs(gameframe_queues.client_mode) { player.changeGameframe(args) }
    }

    private fun Player.openLoginGameframe() {
        val gameframe = gameframes[gameframeTopLevel]
        if (gameframe != null && gameframe.resizable == ui.frameResizable) {
            ifOpenTop(gameframe.topLevel)
            openGameframe(gameframe, eventBus)
            return
        }
        val fallback = selectFallback(ui.frameResizable, stoneArrangements) ?: default
        ui.frameResizable = fallback.resizable
        gameframeTopLevel = fallback.topLevel.id
        stoneArrangements = fallback.stoneArrangement
        ifOpenTop(fallback.topLevel)
        openGameframe(fallback, eventBus)
    }

    private fun Player.queueGameframeMove(gameframe: Gameframe) {
        val settingsClientMode = ui.frameResizable != gameframe.resizable
        if (settingsClientMode) {
            runClientScript(3998, gameframe.clientMode)
        }
        val previous = gameframes.getValue(gameframeTopLevel)
        gameframeTopLevel = gameframe.topLevel.id
        ui.frameResizable = gameframe.resizable

        val queueDelay = if (settingsClientMode) 2 else 1
        val gameframeMove = resolveGameframeMove(from = previous, dest = gameframe)
        softQueue(gameframe_queues.client_mode, queueDelay, gameframeMove)
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
    }

    private fun Player.resolveGameframeMove(from: Gameframe, dest: Gameframe): GameframeMove {
        val intermediate = resolveIntermediate(from, dest)
        return GameframeMove(from = from, dest = dest, intermediate = intermediate)
    }

    /*
     * This is required for emulation purposes and might also be required for an edge case within
     * the client/cs2. This can be seen when going from a fixed gameframe to a resizable one.
     * If the `resizable_stone_arrangement` has to be changed to match the target gameframe, the
     * client will receive two `if_opentop` + `if_movesub` sequences. One going from the current
     * gameframe toplevel to a gameframe toplevel that matches the current stone arrangement var
     * and is resizable, followed by a second `if_opentop` + `if_movesub` group going from this
     * intermediate gameframe to the original target gameframe.
     */
    private fun Player.resolveIntermediate(from: Gameframe, dest: Gameframe): Gameframe? {
        val requiresIntermediate =
            dest.resizable && !from.resizable && stoneArrangements != dest.stoneArrangement
        if (!requiresIntermediate) {
            return null
        }
        return gameframes.values.first { it.hasFlags(resizable = true, stoneArrangements) }
    }

    private fun Player.changeGameframe(move: GameframeMove) {
        val (from, dest, intermediate) = move
        if (dest.resizable) {
            stoneArrangements = dest.stoneArrangement
        }
        resyncVar(varps.chat_filter_assist)
        resyncVar(varps.settings_tracking)

        val sameGameframe = from.topLevel.isType(dest.topLevel)
        if (!sameGameframe) {
            if (intermediate != null) {
                moveGameframe(from, intermediate, eventBus)
                moveGameframe(intermediate, dest, eventBus)
            } else {
                moveGameframe(from, dest, eventBus)
            }
            // TODO(content):
            //  After 1 cycle, cs2 `settings_interface_scaling` and `buff_bar_layout_redraw` are
            //  sent. However, I am unsure how it is scheduled. This action does not seem to be
            //  stalled by modals and does not force-close them. A soft timer seems highly unlikely,
            //  but it would be the only way to achieve this behavior. We will wait until we have
            //  more information before adding this.
        }

        ifOpenOverlay(interfaces.orbs, components.toplevel_target_orbs, eventBus)
        Cinematic.syncMinimapState(this)
    }

    private fun selectFallback(resizable: Boolean, stoneArrangements: Boolean): Gameframe? {
        val priority = gameframes.values.firstOrNull { it.hasFlags(resizable, stoneArrangements) }
        if (priority != null) {
            return priority
        }
        return gameframes.values.firstOrNull { it.resizable == resizable }
    }

    private fun Gameframe.hasFlags(resizable: Boolean, stoneArrangements: Boolean): Boolean {
        return this.resizable == resizable && this.stoneArrangement == stoneArrangements
    }

    private fun loadAll() {
        gameframes = loader.loadGameframes()
        moveEvents = loader.loadMoveEvents().mapMoveEvents()
        default = selectDefault(gameframes.values)
    }

    private fun Map<ComponentType, ComponentType>.mapMoveEvents(): List<MoveEvent> {
        return map { MoveEvent(it.key, it.value) }
    }

    private fun selectDefault(from: Iterable<Gameframe>): Gameframe {
        return from.single(Gameframe::isDefault)
    }

    private data class MoveEvent(val target: ComponentType, val event: ComponentType)
}
