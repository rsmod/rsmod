package org.rsmod.api.type.refs.content

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.content.ContentType
import org.rsmod.game.type.content.ContentTypeBuilder

public abstract class ContentReferences : NameTypeReferences<ContentType>(ContentType::class.java) {
    override fun find(internal: String): ContentType {
        val type = ContentTypeBuilder(internal).build(id = -1)
        cache += type
        return type
    }
}
