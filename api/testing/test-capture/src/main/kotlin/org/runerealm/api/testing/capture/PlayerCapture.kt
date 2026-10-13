package org.runerealm.api.testing.capture

import org.runerealm.game.entity.Player

public fun Player.attachClientCapture(): CaptureClient {
    val capture = CaptureClient()
    client = capture
    return capture
}
