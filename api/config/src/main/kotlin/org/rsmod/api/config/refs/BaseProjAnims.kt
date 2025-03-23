@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.proj.ProjAnimReferences

typealias projanims = BaseProjAnims

object BaseProjAnims : ProjAnimReferences() {
    val arrow = find("arrow")
    val bolt = find("bolt")
    val chinchompa = find("chinchompa")
    val thrown = find("thrown")
    val doublearrow_one = find("doublearrow_one")
    val doublearrow_two = find("doublearrow_two")
}
