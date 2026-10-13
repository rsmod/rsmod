package org.runerealm.api.type.refs.content

import org.runerealm.api.type.refs.NameTypeReferences
import org.runerealm.game.type.content.ContentGroupType
import org.runerealm.game.type.content.ContentGroupTypeBuilder

public abstract class ContentReferences :
    NameTypeReferences<ContentGroupType>(ContentGroupType::class.java) {
    override fun find(internal: String): ContentGroupType {
        val type = ContentGroupTypeBuilder(internal).build(id = -1)
        cache += type
        return type
    }
}
