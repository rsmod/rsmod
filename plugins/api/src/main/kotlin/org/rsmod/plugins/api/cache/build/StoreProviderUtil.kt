package org.rsmod.plugins.api.cache.build

import java.io.File

public object StoreProviderUtil {

    public fun createOrCopyStore(createPath: File, copyPath: File) {
        if (createPath.exists() && createPath.walk().count() > 1) return
        createPath.mkdirs()
        if (copyPath.exists()) {
            copyPath.copyRecursively(createPath)
        }
    }
}
