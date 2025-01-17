@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.bas.BasBuilder

typealias baseanimsets = BaseBas

object BaseBas : BasBuilder() {
    val human_default =
        build("human_default") {
            readyAnim = seqs.human_ready
            turnAnim = seqs.human_turnonspot
            walkAnim = seqs.human_walk_f
            walkAnimBack = seqs.human_walk_b
            walkAnimLeft = seqs.human_walk_l
            walkAnimRight = seqs.human_walk_r
            runAnim = seqs.human_running
        }
}
