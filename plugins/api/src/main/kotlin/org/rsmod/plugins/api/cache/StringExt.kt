package org.rsmod.plugins.api.cache

private val REMOVE_REGEX = Regex("[()]")
private val TYPE_NAME_REGEX = Regex("[^a-zA-Z\\d_:]")

/* Code credits to Bart Helvert */
internal fun String.stripTags(): String {
    if (!contains('<')) return this
    val builder = StringBuilder(length)
    var ignoreNext = false
    forEach { char ->
        if (char == '<') {
            ignoreNext = true
        } else if (char == '>') {
            ignoreNext = false
        } else if (!ignoreNext) {
            builder.append(char)
        }
    }
    return builder.toString()
}

internal fun String.normalizeForNamedMap(): String = this
    .replace(" - ", "_")
    .replace(REMOVE_REGEX, "")
    .replace(TYPE_NAME_REGEX, "_")
