package org.rsmod.plugins.api.net.downstream

import org.rsmod.game.protocol.packet.DownstreamPacket

public data class VarpSmall(val varp: Int, val packed: Int) : DownstreamPacket
public data class VarpLarge(val varp: Int, val packed: Int) : DownstreamPacket
