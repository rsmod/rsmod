package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.category.CategoryReferences

typealias categories = BaseCategories

object BaseCategories : CategoryReferences() {
    val regular_cut_tree = find("regular_cut_tree")
}
