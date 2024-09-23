@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.content.ContentReferences
import org.rsmod.game.type.content.ContentGroupType

public typealias content = BaseContent

public object BaseContent : ContentReferences() {
    public val closed_single_door: ContentGroupType = find("closed_single_door")
    public val opened_single_door: ContentGroupType = find("opened_single_door")
    public val closed_left_door: ContentGroupType = find("closed_left_door")
    public val closed_right_door: ContentGroupType = find("closed_right_door")
    public val opened_left_door: ContentGroupType = find("opened_left_door")
    public val opened_right_door: ContentGroupType = find("opened_right_door")
    public val bookcase: ContentGroupType = find("bookcase")
    public val ladder_down: ContentGroupType = find("ladder_down")
    public val ladder_up: ContentGroupType = find("ladder_up")
    public val ladder_option: ContentGroupType = find("ladder_option")
    public val empty_crate: ContentGroupType = find("empty_crate")
    public val empty_chest: ContentGroupType = find("empty_chest")
    public val empty_boxes: ContentGroupType = find("empty_boxes")
    public val empty_sacks: ContentGroupType = find("empty_sacks")
    public val person: ContentGroupType = find("person")
    public val dungeonladder_down: ContentGroupType = find("dungeonladder_down")
    public val dungeonladder_up: ContentGroupType = find("dungeonladder_up")
    public val spiralstaircase_down: ContentGroupType = find("spiralstaircase_down")
    public val spiralstaircase_up: ContentGroupType = find("spiralstaircase_up")
    public val spiralstaircase_option: ContentGroupType = find("spiralstaircase_option")
    public val duck: ContentGroupType = find("duck")
    public val duckling: ContentGroupType = find("duckling")
    public val woodcutting_axe: ContentGroupType = find("woodcutting_axe")
    public val closed_left_picketgate: ContentGroupType = find("closed_left_picketgate")
    public val closed_right_picketgate: ContentGroupType = find("closed_right_picketgate")
    public val opened_left_picketgate: ContentGroupType = find("opened_left_picketgate")
    public val opened_right_picketgate: ContentGroupType = find("opened_right_picketgate")
    public val ore: ContentGroupType = find("ore")
}
