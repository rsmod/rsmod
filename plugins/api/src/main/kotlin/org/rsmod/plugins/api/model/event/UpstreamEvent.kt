package org.rsmod.plugins.api.model.event

import org.rsmod.game.events.GameEvent
import org.rsmod.game.events.GameKeyedEvent
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedItem

public sealed class UpstreamEvent {

    public data class MoveGameClick(
        val player: Player,
        val mode: Int,
        val coords: Coordinates
    ) : GameEvent

    public data class ClientCheat(
        val player: Player,
        val text: String,
        val args: List<String>
    ) : GameEvent

    public data class IfButton(
        val player: Player,
        val clickType: Int,
        val component: NamedComponent,
        val dynamicChild: Int?,
        val item: NamedItem?
    ) : GameKeyedEvent
}
