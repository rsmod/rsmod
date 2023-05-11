package org.rsmod.game.model.mob.list

import org.rsmod.game.model.mob.Mob

public inline fun <T : Mob> MobList<T>.parallelForEachNotNull(action: (T) -> Unit) {
    for (element in parallelStream()) {
        action(element ?: continue)
    }
}

public inline fun <T : Mob> MobList<T>.forEachNotNull(action: (T) -> Unit) {
    for (element in this) {
        action(element ?: continue)
    }
}

public inline fun <T : Mob> MobList<T>.anyNotNull(predicate: (T) -> Boolean): Boolean {
    for (element in this) {
        if (predicate(element ?: continue)) {
            return true
        }
    }
    return false
}

public fun <T : Mob> MobList<T>.countNotNull(): Int = count { it != null }
