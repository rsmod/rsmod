package org.rsmod.api.type.refs.model

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.model.HashedModelType
import org.rsmod.game.type.model.ModelType

public abstract class ModelReferences : HashTypeReferences<ModelType>(ModelType::class.java) {
    override fun find(internal: String, hash: Long?): ModelType {
        val type = HashedModelType(hash, internal)
        cache += type
        return type
    }
}
