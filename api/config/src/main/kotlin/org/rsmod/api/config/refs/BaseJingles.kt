@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.jingle.JingleReferences

typealias jingles = BaseJingles

object BaseJingles : JingleReferences() {
    val emote_air_guitar = find("emote_air_guitar")
}
