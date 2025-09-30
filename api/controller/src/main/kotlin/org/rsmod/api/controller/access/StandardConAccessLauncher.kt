package org.rsmod.api.controller.access

import org.rsmod.game.entity.Controller

public class StandardConAccessLauncher {
    public fun launch(controller: Controller, block: suspend StandardConAccess.() -> Unit) {
        check(!controller.isDelayed) { "Controller must not be delayed: $controller" }
        controller.launch {
            val standardAccess = StandardConAccess(controller, this)
            block(standardAccess)
        }
    }
}
