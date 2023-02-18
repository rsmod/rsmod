package org.rsmod.plugins.info.player

object PlayerInfoOpcodes {

    const val READ_SKIP_COUNT_OPCODE = 0
    const val READ_AVATAR_INFO_OPCODE = 1

    const val READ_SKIP_COUNT_NO_BITS = 0
    const val READ_SKIP_COUNT_5BITS = 1
    const val READ_SKIP_COUNT_8BITS = 2
    const val READ_SKIP_COUNT_11BITS = 3

    const val READ_HIGH_RES_COORDS_DISPLACEMENT = 0
    const val READ_HIGH_RES_COORDS_3BIT_CHANGE = 1
    const val READ_HIGH_RES_COORDS_4BIT_CHANGE = 2
    const val READ_HIGH_RES_COORDS_TELEPORT_CHANGE = 3

    const val READ_CHANGE_RESOLUTION = 0
}
