package org.runerealm.api.net.rsprot.player

import org.runerealm.api.config.constants
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.protect.ProtectedAccessContextFactory
import org.runerealm.game.entity.Player
import org.runerealm.game.movement.MoveSpeed
import org.runerealm.map.CoordGrid
import org.runerealm.routefinder.collision.CollisionFlagMap

internal fun Player.protectedTelejump(collision: CollisionFlagMap, dest: CoordGrid): Boolean {
    if (isAccessProtected) {
        return false
    }
    launch {
        val context = ProtectedAccessContextFactory.empty()
        val access = ProtectedAccess(this@protectedTelejump, this, context)
        access.telejump(dest, collision)
    }
    return true
}

internal fun Player.modLevelTeleMoveSpeed(developmentMode: Boolean): MoveSpeed? =
    if (modLevel.clientCode == constants.mod_clientcode_jmod || developmentMode) {
        MoveSpeed.Stationary
    } else {
        null
    }
