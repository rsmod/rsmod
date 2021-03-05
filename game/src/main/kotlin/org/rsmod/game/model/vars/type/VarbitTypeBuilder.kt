package org.rsmod.game.model.vars.type

private const val DEFAULT_ID = -1
private const val DEFAULT_VARP = -1
private const val DEFAULT_LSB = -1
private const val DEFAULT_MSB = -1

@DslMarker
private annotation class VarbitBuilderDslMarker

@VarbitBuilderDslMarker
class VarbitTypeBuilder(
    var id: Int = DEFAULT_ID,
    var varp: Int = DEFAULT_VARP,
    var lsb: Int = DEFAULT_LSB,
    var msb: Int = DEFAULT_MSB
) {

    fun build(): VarbitType {
        check(id != DEFAULT_ID)
        check(varp != DEFAULT_VARP)
        check(lsb != DEFAULT_LSB)
        check(msb != DEFAULT_MSB)
        return VarbitType(id, varp, lsb, msb)
    }
}
