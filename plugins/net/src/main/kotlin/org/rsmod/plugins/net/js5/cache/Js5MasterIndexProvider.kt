package org.rsmod.plugins.net.js5.cache

import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.MasterIndexFormat
import org.openrs2.cache.Store
import javax.inject.Inject
import javax.inject.Provider

public class Js5MasterIndexProvider @Inject constructor(
    @Js5Store private val store: Store
) : Provider<Js5MasterIndex> {

    override fun get(): Js5MasterIndex {
        val masterIndex = Js5MasterIndex.create(store)
        masterIndex.format = MasterIndexFormat.VERSIONED
        return masterIndex
    }
}
