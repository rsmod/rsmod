package org.runerealm.api.type.refs.dbtable

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.dbtable.DbTableType
import org.runerealm.game.type.dbtable.HashedDbTableType

public abstract class DbTableReferences : HashTypeReferences<DbTableType>(DbTableType::class.java) {
    override fun find(internal: String, hash: Long?): DbTableType {
        val type = HashedDbTableType(hash, internal)
        cache += type
        return type
    }
}
