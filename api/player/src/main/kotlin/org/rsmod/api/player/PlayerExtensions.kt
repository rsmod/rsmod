package org.rsmod.api.player

import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed

public fun Player.clearInteractionRoute() {
    clearInteraction()
    abortRoute()
    clearMapFlag()
}

/**
 * This function should only be called directly under specific circumstances. Prefer calling
 * [org.rsmod.api.player.protect.ProtectedAccess.clearPendingAction] instead.
 */
public fun Player.clearPendingAction(eventBus: EventBus) {
    cancelActiveCoroutine()
    clearInteraction()
    ifCloseModals(eventBus)
}

// TODO: invert run mode setting to disable this
public fun Player.ctrlMoveSpeed(): MoveSpeed =
    if (varMoveSpeed == MoveSpeed.Run) {
        MoveSpeed.Walk
    } else {
        MoveSpeed.Run
    }

/**
 * Returns the temp move speed that should be used when the player has used the meta key combination
 * to teleport during [net.rsprot.protocol.game.incoming.misc.user.MoveGameClick] or
 * [net.rsprot.protocol.game.incoming.misc.user.MoveMinimapClick].
 */
// TODO: app config concept to switch "development mode" flag. Can remove the flag arg from here
//  then and simply have it as a branch where if dev mode is on it'll just return Stationary move
//  speed instead of even calling this method.
public fun Player.modLevelTeleMoveSpeed(developmentMode: Boolean): MoveSpeed? =
    if (modGroup?.isAdmin == true || developmentMode) {
        MoveSpeed.Stationary
    } else {
        null
    }
