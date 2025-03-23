package org.rsmod.api.type.refs.proj

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.proj.HashedProjAnimType
import org.rsmod.game.type.proj.ProjAnimType

public abstract class ProjAnimReferences :
    NameTypeReferences<ProjAnimType>(ProjAnimType::class.java) {
    public override fun find(internal: String): ProjAnimType {
        // For now, can't see a realistic situation where identity hash verification is required.
        // Though maybe at some point plugins may require support for this to ensure any base/core
        // projanims are not changed. Can reconsider this decision in the future.
        val type = HashedProjAnimType(null, internal)
        cache += type
        return type
    }
}
