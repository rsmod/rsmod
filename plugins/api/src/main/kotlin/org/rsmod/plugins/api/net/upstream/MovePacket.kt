package org.rsmod.plugins.api.net.upstream

import org.rsmod.protocol.game.packet.UpstreamPacket

public data class MoveGameClick(
    val mode: Int,
    val x: Int,
    val y: Int
) : UpstreamPacket

public data class MoveMinimapClick(
    val mode: Int,
    val x: Int,
    val y: Int,
    val fineX: Int,
    val fineY: Int,
    val minimapPxOffX: Int,
    val minimapPxOffY: Int,
    val cameraAngle: Int
) : UpstreamPacket
