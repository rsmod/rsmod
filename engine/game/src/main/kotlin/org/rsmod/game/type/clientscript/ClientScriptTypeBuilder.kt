package org.rsmod.game.type.clientscript

public class ClientScriptTypeBuilder(public var internal: String? = null) {
    public var intLocalCount: Int? = null
    public var stringLocalCount: Int? = null
    public var intArgumentCount: Int? = null
    public var stringArgumentCount: Int? = null
    public var intOperands: IntArray? = null
    public var stringOperands: Array<String?>? = null
    public var commands: IntArray? = null
    public var switches: Array<Map<Int, Int>>? = null

    public fun build(id: Int): UnpackedClientScriptType {
        val intLocalCount = intLocalCount ?: error("`intLocalCount` must be set.")
        val stringLocalCount = stringLocalCount ?: error("`stringLocalCount` must be set.")
        val intArgumentCount = intArgumentCount ?: error("`intArgumentCount` must be set.")
        val stringArgumentCount = stringArgumentCount ?: error("`stringArgumentCount` must be set.")
        val intOperands = intOperands ?: error("`intOperands` must be set.")
        val stringOperands = stringOperands ?: error("`stringOperands` must be set.")
        val commands = commands ?: error("`commands` must be set.")
        val switches = switches ?: error("`switches` must be set.")
        return UnpackedClientScriptType(
            intLocalCount = intLocalCount,
            stringLocalCount = stringLocalCount,
            intArgumentCount = intArgumentCount,
            stringArgumentCount = stringArgumentCount,
            intOperands = intOperands,
            stringOperands = stringOperands,
            commands = commands,
            switches = switches,
            internalId = id,
            internalName = internal,
        )
    }
}
