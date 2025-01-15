package org.rsmod.api.utils.format

public fun String.addArticle(): String = "${getArticle()} $this"

public fun String.getArticle(): String =
    when (first().lowercaseChar()) {
        'a',
        'e',
        'i',
        'o',
        'u' -> "an"
        else -> "a"
    }
