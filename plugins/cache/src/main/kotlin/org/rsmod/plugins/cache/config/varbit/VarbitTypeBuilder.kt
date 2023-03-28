package org.rsmod.plugins.cache.config.varbit

private const val DEFAULT_ID = -1
private const val DEFAULT_VARP = 0
private const val DEFAULT_BITPOS = -1
private const val DEFAULT_TRANSMIT_FLAG = true

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
public class VarbitTypeBuilder(
    public var id: Int = DEFAULT_ID,
    public var name: String? = null,
    public var varp: Int = DEFAULT_VARP,
    public var lsb: Int = DEFAULT_BITPOS,
    public var msb: Int = DEFAULT_BITPOS,
    public var transmit: Boolean = DEFAULT_TRANSMIT_FLAG
) {

    public fun build(): VarbitType {
        check(id != DEFAULT_ID)
        check(varp != DEFAULT_VARP)
        check(lsb != DEFAULT_BITPOS)
        check(msb != DEFAULT_BITPOS)
        check(lsb <= msb) { "Least-significant bit must be less than or equal to most-significant bit." }
        return VarbitType(id, name, varp, lsb, msb, transmit)
    }

    public operator fun plusAssign(other: VarbitType) {
        if (id == DEFAULT_ID) id = other.id
        if (name == null) name = other.name
        if (varp == DEFAULT_VARP) varp = other.varp
        if (lsb == DEFAULT_BITPOS) lsb = other.lsb
        if (msb == DEFAULT_BITPOS) msb = other.msb
        if (transmit == DEFAULT_TRANSMIT_FLAG) transmit = other.transmit
    }
}
