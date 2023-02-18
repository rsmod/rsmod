package org.rsmod.game.model.mob.list

import org.rsmod.game.model.mob.Mob

public inline fun <T : Mob> MobList<T>.forEachNotNull(action: (T) -> Unit) {
    for (element in this) {
        action(element ?: continue)
    }
}
