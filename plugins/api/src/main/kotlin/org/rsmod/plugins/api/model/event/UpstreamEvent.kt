package org.rsmod.plugins.api.model.event

import org.rsmod.game.map.Coordinates
import org.rsmod.plugins.api.move.MoveSpeed
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedItem

public object UpstreamEvent {

    public data class MoveGameClick(
        val speed: SpeedRequest,
        val coords: Coordinates
    ) : TypePlayerEvent {

        public fun moveSpeed(): MoveSpeed? = when (speed) {
            Neutral -> null
            Teleport -> MoveSpeed.Displace
            TempRun -> MoveSpeed.Run
        }

        public sealed class SpeedRequest
        public object Neutral : SpeedRequest()
        public object TempRun : SpeedRequest()
        public object Teleport : SpeedRequest()

        public companion object {

            public fun speedRequest(mode: Int): SpeedRequest = when (mode) {
                1 -> TempRun
                2 -> Teleport
                else -> Neutral
            }
        }
    }

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
