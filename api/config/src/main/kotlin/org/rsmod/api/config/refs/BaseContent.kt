@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.content.ContentReferences

typealias content = BaseContent

object BaseContent : ContentReferences() {
    val closed_single_door = find("closed_single_door")
    val opened_single_door = find("opened_single_door")
    val closed_left_door = find("closed_left_door")
    val closed_right_door = find("closed_right_door")
    val opened_left_door = find("opened_left_door")
    val opened_right_door = find("opened_right_door")
    val bookcase = find("bookcase")
    val ladder_down = find("ladder_down")
    val ladder_up = find("ladder_up")
    val ladder_option = find("ladder_option")
    val empty_crate = find("empty_crate")
    val empty_chest = find("empty_chest")
    val empty_boxes = find("empty_boxes")
    val empty_sacks = find("empty_sacks")
    val person = find("person")
    val dungeonladder_down = find("dungeonladder_down")
    val dungeonladder_up = find("dungeonladder_up")
    val spiralstaircase_down = find("spiralstaircase_down")
    val spiralstaircase_up = find("spiralstaircase_up")
    val spiralstaircase_option = find("spiralstaircase_option")
    val duck = find("duck")
    val duckling = find("duckling")
    val woodcutting_axe = find("woodcutting_axe")
    val closed_left_picketgate = find("closed_left_picketgate")
    val closed_right_picketgate = find("closed_right_picketgate")
    val opened_left_picketgate = find("opened_left_picketgate")
    val opened_right_picketgate = find("opened_right_picketgate")
    val ore = find("ore")
    val tree = find("tree")
}
