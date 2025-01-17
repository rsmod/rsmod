@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.varobjbit.VarObjBitBuilder

typealias varobjs = BaseVarObjBits

object BaseVarObjBits : VarObjBitBuilder() {
    val charges_16383 =
        build("charges_16383") {
            startBit = 0
            endBit = 13
        }

    val snakeboss_blowpipe_darrtype =
        build("snakeboss_blowpipe_darttype") {
            startBit = 0
            endBit = 3
        }

    val snakeboss_blowpipe_dartcount =
        build("snakeboss_blowpipe_dartcount") {
            startBit = 4
            endBit = 17
        }

    val snakeboss_blowpipe_flakes =
        build("snakeboss_blowpipe_flakes") {
            startBit = 18
            endBit = 31
        }
}
