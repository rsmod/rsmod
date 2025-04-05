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
    val magic_spell = find("magic_spell")
    val magic_spell_low = find("magic_spell_low")
    val iban_blast = find("iban_blast")
    val vulnerability = find("vulnerability")
    val stun = find("stun")
    val crumble_undead = find("crumble_undead")
    val enfeeble = find("enfeeble")
    val confuse = find("confuse")
    val bind = find("bind")
    val tumekens_shadow = find("tumekens_shadow")
}
