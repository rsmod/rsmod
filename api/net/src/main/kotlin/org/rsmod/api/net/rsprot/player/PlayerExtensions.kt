package org.rsmod.api.net.rsprot.player

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

/**
 * Returns the temp move speed that should be used when the player has used the meta key combination
 * to teleport during [net.rsprot.protocol.game.incoming.misc.user.MoveGameClick] or
 * [net.rsprot.protocol.game.incoming.misc.user.MoveMinimapClick].
 */
// TODO: app config concept to switch "development mode" flag. Can remove the flag arg from here
//  then and simply have it as a branch where if dev mode is on it'll just return Stationary move
//  speed instead of even calling this method.
internal fun Player.modLevelTeleMoveSpeed(developmentMode: Boolean): MoveSpeed? =
    if (modGroup?.isClientAdmin == true || developmentMode) {
        MoveSpeed.Stationary
    } else {
        null
    }
