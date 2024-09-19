@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.content.ContentReferences
import org.rsmod.game.type.content.ContentType

public typealias content = BaseContent

public object BaseContent : ContentReferences() {
    public val closed_single_door: ContentType = find("closed_single_door")
    public val opened_single_door: ContentType = find("opened_single_door")
    public val closed_left_door: ContentType = find("closed_left_door")
    public val closed_right_door: ContentType = find("closed_right_door")
    public val opened_left_door: ContentType = find("opened_left_door")
    public val opened_right_door: ContentType = find("opened_right_door")
    public val bookcase: ContentType = find("bookcase")
    public val ladder_down: ContentType = find("ladder_down")
    public val ladder_up: ContentType = find("ladder_up")
    public val ladder_option: ContentType = find("ladder_option")
    public val empty_crate: ContentType = find("empty_crate")
    public val empty_chest: ContentType = find("empty_chest")
    public val empty_boxes: ContentType = find("empty_boxes")
    public val empty_sacks: ContentType = find("empty_sacks")
    public val person: ContentType = find("person")
    public val dungeonladder_down: ContentType = find("dungeonladder_down")
    public val dungeonladder_up: ContentType = find("dungeonladder_up")
    public val spiralstaircase_down: ContentType = find("spiralstaircase_down")
    public val spiralstaircase_up: ContentType = find("spiralstaircase_up")
    public val spiralstaircase_option: ContentType = find("spiralstaircase_option")
    public val duck: ContentType = find("duck")
    public val duckling: ContentType = find("duckling")
}
