package org.rsmod.api.testing.util

import org.rsmod.api.realm.RealmConfig
import org.rsmod.map.CoordGrid

internal object TestRealmConfig {
    fun create(): RealmConfig =
        RealmConfig(
            id = 0,
            loginMessage = null,
            loginBroadcast = null,
            baseXpRate = 1.0,
            globalXpRate = 1.0,
            spawnCoord = CoordGrid(0, 50, 50, 0, 0),
            respawnCoord = CoordGrid(0, 50, 50, 0, 0),
            devMode = false,
            requireRegistration = false,
            ignorePasswords = true,
            autoAssignDisplayNames = true,
        )
}
