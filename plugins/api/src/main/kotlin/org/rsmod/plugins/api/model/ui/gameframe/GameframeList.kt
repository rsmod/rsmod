package org.rsmod.plugins.api.model.ui.gameframe

import com.github.michaelbull.logging.InlineLogger
import org.rsmod.plugins.api.cache.name.ui.ComponentNameMap
import org.rsmod.plugins.api.cache.name.ui.UserInterfaceNameMap
import javax.inject.Inject

private val logger = InlineLogger()

class GameframeList(
    private val interfaces: UserInterfaceNameMap,
    private val components: ComponentNameMap,
    private val frames: MutableMap<GameframeType, Gameframe>
) : Map<GameframeType, Gameframe> by frames {

    @Inject
    constructor(
        interfaces: UserInterfaceNameMap,
        components: ComponentNameMap
    ) : this(interfaces, components, mutableMapOf())

    fun register(frame: Gameframe) {
        check(!frames.containsKey(frame.type)) { "Gameframe with type already exists (type=${frame.type})" }
        logger.debug { "Register gameframe (frame=$frame)" }
        frames[frame.type] = frame
    }

    fun register(init: GameframeBuilder.() -> Unit) {
        val builder = GameframeBuilder().apply(init)
        val gameframe = builder.build(interfaces, components)
        register(gameframe)
    }
}
