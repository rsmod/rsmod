package org.rsmod.plugins.api.util

fun String.plural(count: Int): String {
    if (count == 1 || endsWith('s')) {
        return this
    }
    return this + "s"
}
