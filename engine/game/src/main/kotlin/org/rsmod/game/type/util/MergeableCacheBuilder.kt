package org.rsmod.game.type.util

import org.rsmod.game.type.CacheType

public fun interface MergeableCacheBuilder<T : CacheType> {
    public fun merge(edit: T, base: T): T
}
