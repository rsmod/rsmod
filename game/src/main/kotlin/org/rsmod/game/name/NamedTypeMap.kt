package org.rsmod.game.name

private val VALID_REGEX = Regex("^[a-z0-9-_.]*$")

private fun String.isValid() = VALID_REGEX.matches(this)

open class NamedTypeMap<T>(
    private val types: MutableMap<String, T> = mutableMapOf()
) : Map<String, T> by types {

    override operator fun get(key: String): T? {
        key.validate()
        return types[key]
    }

    operator fun set(name: String, type: T) {
        name.validate()
        types[name] = type
    }

    private fun String.validate() {
        if (!isValid()) {
            error("Name \"$this\" must be all lowercase with underscore (_) for whitespaces.")
        }
    }
}
