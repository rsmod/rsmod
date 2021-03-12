package org.rsmod.game.timer

class TimerKey(
    val persistenceKey: String? = null,
    val tickOffline: Boolean = true
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimerKey

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
