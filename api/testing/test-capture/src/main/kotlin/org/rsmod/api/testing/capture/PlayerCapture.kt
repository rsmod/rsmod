package org.rsmod.api.testing.capture

import org.rsmod.game.client.Client
import org.rsmod.game.entity.Player

public fun Player.attachClientCapture(): CaptureClient {
    val capture = CaptureClient()
    client = capture as Client<Any, Any>
    return capture
}
