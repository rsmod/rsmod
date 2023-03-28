package org.rsmod.plugins.cache.packer.util

internal object StringUtils {

    fun String.stripTag(): String {
        if (indexOf('.') == -1) return this
        return substring(indexOf('.') + 1, length)
    }
}
