@file:Suppress("SpellCheckingInspection", "unused")

package org.runerealm.api.config.refs

import org.runerealm.api.type.refs.mod.ModLevelReferences

typealias modlevels = BaseModLevels

object BaseModLevels : ModLevelReferences() {
    val player = find("player")
    val moderator = find("moderator")
    val admin = find("admin")
    val owner = find("owner")
}
