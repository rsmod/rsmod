package org.rsmod.plugins.api.model.event

import org.rsmod.game.events.GameKeyedEvent
import org.rsmod.game.model.mob.Player
import org.rsmod.game.types.NamedComponent
import org.rsmod.game.types.NamedInterface

public sealed class UIEvent {

    public data class OpenTopLevel(
        val player: Player,
        val topLevel: NamedInterface
    ) : GameKeyedEvent

    public data class OpenOverlay(
        val player: Player,
        val overlay: NamedInterface,
        val target: NamedComponent
    ) : GameKeyedEvent
}
