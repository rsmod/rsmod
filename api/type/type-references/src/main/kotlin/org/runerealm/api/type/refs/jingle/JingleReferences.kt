package org.runerealm.api.type.refs.jingle

import org.runerealm.api.type.refs.NameTypeReferences
import org.runerealm.game.type.jingle.JingleType
import org.runerealm.game.type.jingle.JingleTypeBuilder

public abstract class JingleReferences : NameTypeReferences<JingleType>(JingleType::class.java) {
    override fun find(internal: String): JingleType {
        val type = JingleTypeBuilder(internal).build(id = -1)
        cache += type
        return type
    }
}
