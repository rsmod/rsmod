package gg.rsmod.plugins.api.protocol.codec.account

import gg.rsmod.game.model.client.Client
import gg.rsmod.plugins.api.protocol.Device
import gg.rsmod.util.security.IsaacRandom
import io.netty.channel.Channel

data class Account(
    val channel: Channel,
    val client: Client,
    val device: Device,
    val decodeIsaac: IsaacRandom,
    val encodeIsaac: IsaacRandom
)
