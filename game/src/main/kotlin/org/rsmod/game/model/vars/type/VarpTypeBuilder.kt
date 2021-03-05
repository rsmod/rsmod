package org.rsmod.game.model.vars.type

private const val DEFAULT_ID = -1
private const val DEFAULT_TYPE = 0

@DslMarker
private annotation class VarpBuilderDslMarker

@VarpBuilderDslMarker
class VarpTypeBuilder(
    var id: Int = DEFAULT_ID,
    var type: Int = DEFAULT_TYPE
) {

    fun build(): VarpType {
        check(id != DEFAULT_ID)
        return VarpType(id, type)
    }
}
