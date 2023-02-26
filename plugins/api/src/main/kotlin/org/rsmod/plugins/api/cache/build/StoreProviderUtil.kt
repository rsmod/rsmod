package org.rsmod.plugins.api.cache.build

import java.io.File

internal object StoreProviderUtil {

    fun createOrCopyStore(createPath: File, copyPath: File) {
        if (createPath.exists() && createPath.walk().count() > 1) return
        createPath.mkdirs()
        if (copyPath.exists()) {
            copyPath.copyRecursively(createPath)
        }
    }
}
