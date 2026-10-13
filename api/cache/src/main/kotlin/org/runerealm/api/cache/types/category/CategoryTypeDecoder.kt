package org.runerealm.api.cache.types.category

import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.category.CategoryType
import org.runerealm.game.type.category.CategoryTypeList

public object CategoryTypeDecoder {
    public fun decodeAll(nameMapping: NameMapping): CategoryTypeList =
        CategoryTypeList(nameMapping.toTypeMap())

    private fun NameMapping.toTypeMap(): Map<Int, CategoryType> =
        categories.entries.associate { it.value to CategoryType(it.value, it.key) }
}
