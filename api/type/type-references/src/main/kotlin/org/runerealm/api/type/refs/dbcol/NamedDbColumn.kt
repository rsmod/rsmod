package org.runerealm.api.type.refs.dbcol

import org.runerealm.game.dbtable.DbColumn
import org.runerealm.game.type.literal.CacheVarLiteral

public data class NamedDbColumn(
    val internalName: String,
    val column: DbColumn<*, *>,
    val types: List<CacheVarLiteral>,
)
