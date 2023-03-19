package org.rsmod.plugins.api.net.upstream

import org.rsmod.plugins.api.movement.MoveSpeed
import org.rsmod.protocol.game.packet.UpstreamPacket

public data class MoveGameClick(
    val mode: Int,
    val x: Int,
    val z: Int
) : UpstreamPacket {

    public fun speed(): MoveSpeed? = mode.toSpeed()
}

public data class MoveMinimapClick(
    val mode: Int,
    val x: Int,
    val z: Int,
    val fineX: Int,
    val fineY: Int,
    val minimapPxOffX: Int,
    val minimapPxOffY: Int,
    val cameraAngle: Int
) : UpstreamPacket {

    public fun speed(): MoveSpeed? = mode.toSpeed()
}

private fun Int.toSpeed(): MoveSpeed? = MoveSpeed.values.firstOrNull { it.id == this }
