package org.rsmod.game.type.varp

public enum class VarpTransmitLevel(public val id: Int) {
    /** Varp is never transmitted to clients. */
    Never(0),
    /** Varp is transmitted to clients only when its value has changed. */
    OnSetDifferent(1),
    /** Varp will always transmit to clients even when its value has not changed. */
    OnSetAlways(2);

    public val never: Boolean
        get() = this == Never

    public val always: Boolean
        get() = this == OnSetAlways

    public val onDiff: Boolean
        get() = this == OnSetDifferent

    public companion object {
        public operator fun get(id: Int): VarpTransmitLevel? =
            when (id) {
                Never.id -> Never
                OnSetDifferent.id -> OnSetDifferent
                OnSetAlways.id -> OnSetAlways
                else -> null
            }
    }
}
