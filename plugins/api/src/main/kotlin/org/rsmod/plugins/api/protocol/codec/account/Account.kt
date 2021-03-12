package org.rsmod.plugins.api.protocol.codec.account

import io.netty.channel.Channel
import org.rsmod.game.model.client.Client
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.util.security.IsaacRandom

data class Account(
    val channel: Channel,
    val client: Client,
    val device: Device,
    val decodeIsaac: IsaacRandom,
    val encodeIsaac: IsaacRandom,
    val newAccount: Boolean
)
