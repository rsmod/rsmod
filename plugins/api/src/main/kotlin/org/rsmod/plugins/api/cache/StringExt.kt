package org.rsmod.plugins.api.cache

import java.net.URL

private object StringExt

internal fun String.toResourceUrl(): URL? {
    return StringExt::class.java.getResource(this)
}
