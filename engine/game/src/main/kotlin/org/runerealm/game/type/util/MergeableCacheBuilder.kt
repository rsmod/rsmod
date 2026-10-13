package org.runerealm.game.type.util

import org.runerealm.game.type.CacheType

public fun interface MergeableCacheBuilder<T : CacheType> {
    public fun merge(edit: T, base: T): T
}
