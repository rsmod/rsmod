package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.category.CategoryReferences
import org.rsmod.game.type.category.CategoryType

public typealias categories = BaseCategories

public object BaseCategories : CategoryReferences() {
    public val regular_cut_tree: CategoryType = find("regular_cut_tree")
}
