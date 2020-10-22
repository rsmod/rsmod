package gg.rsmod.game.attribute

@Suppress("UNUSED")
class AttributeKey<T>(
    val persistenceKey: String? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttributeKey<*>

        return if (persistenceKey != null) {
            other.persistenceKey == persistenceKey
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return persistenceKey?.hashCode() ?: super.hashCode()
    }
}

class AttributeMap(
    private val attributes: MutableMap<AttributeKey<*>, Any> = mutableMapOf()
) : Iterable<Map.Entry<AttributeKey<*>, Any>> {

    val size: Int
        get() = attributes.size

    fun contains(key: AttributeKey<*>): Boolean {
        return attributes.contains(key)
    }

    fun isEmpty(): Boolean = attributes.isEmpty()

    fun isNotEmpty(): Boolean = attributes.isNotEmpty()

    operator fun set(key: AttributeKey<*>, value: Any?) {
        if (value != null) {
            attributes[key] = value
        } else {
            attributes.remove(key)
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: AttributeKey<T>): T? {
        return attributes[key] as? T
    }

    override fun iterator(): Iterator<Map.Entry<AttributeKey<*>, Any>> = attributes.iterator()
}
