package org.rsmod.api.net.rsprot.player

import org.rsmod.api.config.constants
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessContextFactory
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.collision.CollisionFlagMap

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
