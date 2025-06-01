package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.area.AreaReferences

typealias areas = BaseAreas

object BaseAreas : AreaReferences() {
    val lumbridge = find("lumbridge")
    val singles_plus = find("singles_plus")
    val multiway = find("multiway")
}
