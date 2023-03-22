package org.rsmod.plugins.api.model.event

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.mob.move.MovementSpeed
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedItem

public object UpstreamEvent {

    public data class MoveGameClick(
        val speed: MovementSpeed,
        val coords: Coordinates
    ) : TypePlayerEvent

    public data class ClientCheat(
        val text: String,
        val args: List<String>
    ) : TypePlayerEvent

    public data class IfButton(
        val clickType: Int,
        val component: NamedComponent,
        val dynamicChild: Int?,
        val item: NamedItem?
    ) : TypePlayerKeyedEvent
}
