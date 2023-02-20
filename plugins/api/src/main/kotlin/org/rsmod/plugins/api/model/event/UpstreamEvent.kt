package org.rsmod.plugins.api.model.event

import org.rsmod.game.events.Event
import org.rsmod.game.model.mob.Player
import org.rsmod.game.types.NamedComponent
import org.rsmod.game.types.NamedItem

public sealed class UpstreamEvent : Event {

    public data class MoveGameClick(
        val player: Player,
        val mode: Int,
        val x: Int,
        val y: Int
    ) : UpstreamEvent()

    public data class ClientCheat(
        val player: Player,
        val text: String,
        val args: List<String>
    ) : UpstreamEvent()

    public data class IfButton(
        val player: Player,
        val clickType: Int,
        val component: NamedComponent,
        val dynamicChild: Int?,
        val item: NamedItem?
    ) : UpstreamEvent()
}
