package org.rsmod.plugins.cache.packer

internal object StringUtil {

    fun String.stripTag(): String {
        if (indexOf('.') == -1) return this
        return substring(indexOf('.') + 1, length)
    }
}
