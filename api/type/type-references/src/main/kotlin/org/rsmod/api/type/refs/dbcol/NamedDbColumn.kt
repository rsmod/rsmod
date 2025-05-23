package org.rsmod.api.type.refs.dbcol

import org.rsmod.game.dbtable.DbColumn
import org.rsmod.game.type.literal.CacheVarLiteral

public data class NamedDbColumn(
    val internalName: String,
    val column: DbColumn<*, *>,
    val types: List<CacheVarLiteral>,
)
