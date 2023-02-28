package org.rsmod.plugins.types

@JvmInline
public value class NamedComponent(public val id: Int) {

    public val interfaceId: Int get() = (id shr 16) and 0xFFFF

    public val child: Int get() = id and 0xFFFF

    public constructor(interfaceId: Int, child: Int) : this(
        (interfaceId shl 16) or (child and 0xFFFF)
    )

    @Suppress("NOTHING_TO_INLINE")
    public inline fun parent(): NamedInterface {
        return NamedInterface(interfaceId)
    }

    public operator fun component1(): Int = interfaceId

    public operator fun component2(): Int = child

    public override fun toString(): String {
        return "NamedComponent(interface=$interfaceId, child=$child)"
    }
}
