package org.rsmod.api.type.refs.dbrow

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.dbrow.HashedDbRowType

public abstract class DbRowReferences : HashTypeReferences<DbRowType>(DbRowType::class.java) {
    override fun find(internal: String, hash: Long?): DbRowType {
        val type = HashedDbRowType(hash, internal)
        cache += type
        return type
    }
}
