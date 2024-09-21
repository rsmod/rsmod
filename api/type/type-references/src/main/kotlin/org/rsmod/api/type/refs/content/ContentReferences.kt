package org.rsmod.api.type.refs.content

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.content.ContentGroupTypeBuilder

public abstract class ContentReferences :
    NameTypeReferences<ContentGroupType>(ContentGroupType::class.java) {
    override fun find(internal: String): ContentGroupType {
        val type = ContentGroupTypeBuilder(internal).build(id = -1)
        cache += type
        return type
    }
}
