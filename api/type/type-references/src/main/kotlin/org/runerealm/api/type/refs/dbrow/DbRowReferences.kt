package org.runerealm.api.type.refs.dbrow

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.dbrow.DbRowType
import org.runerealm.game.type.dbrow.HashedDbRowType

public abstract class DbRowReferences : HashTypeReferences<DbRowType>(DbRowType::class.java) {
    override fun find(internal: String, hash: Long?): DbRowType {
        val type = HashedDbRowType(hash, internal)
        cache += type
        return type
    }
}
