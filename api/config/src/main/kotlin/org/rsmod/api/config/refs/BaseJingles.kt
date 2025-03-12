@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.jingle.JingleReferences

typealias jingles = BaseJingles

object BaseJingles : JingleReferences() {
    val death_jingle = find("death_jingle")
    val death_jingle_2 = find("death_jingle_2")
    val emote_air_guitar = find("emote_air_guitar")
}
