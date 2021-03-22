package org.rsmod.plugins.api.privilege

import org.rsmod.game.privilege.Privilege

object Privileges {

    object Mod : Privilege(
        nameId = "mod",
        displayName = "Moderator",
        clientId = 1
    )

    object Admin : Privilege(
        nameId = "admin",
        displayName = "Administrator",
        clientId = 2
    )
}
