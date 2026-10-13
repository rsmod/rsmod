package org.runerealm.api.type.refs.dbcol

import jakarta.inject.Inject
import kotlin.math.max
import org.runerealm.api.type.refs.TypeReferences
import org.runerealm.api.type.refs.resolver.TypeReferenceResolver
import org.runerealm.api.type.refs.resolver.TypeReferenceResult
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.ImplicitNameNotFound
import org.runerealm.api.type.refs.resolver.err
import org.runerealm.api.type.refs.resolver.ok
import org.runerealm.api.type.refs.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.dbtable.DbTableTypeList
import org.runerealm.game.type.literal.CacheVarLiteral

public class DbColumnReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val tables: DbTableTypeList) :
    TypeReferenceResolver<NamedDbColumn, Nothing> {
    private val names: Map<String, Int>
        get() = nameMapping.dbColumns

    override fun resolve(refs: TypeReferences<NamedDbColumn, Nothing>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun NamedDbColumn.resolve(): TypeReferenceResult {
        val internalId = names[internalName] ?: return err(ImplicitNameNotFound(internalName))
        TypeResolver[column] = internalName
        TypeResolver[column] = internalId

        val table = tables[column.table] ?: return update(TypeReferenceResult.CacheTypeNotFound)
        val tableColumnTypes = table.types[column.columnId] ?: emptyList()

        val max = max(tableColumnTypes.size, types.size)
        for (i in 0 until max) {
            val tableColumnType = tableColumnTypes.getOrNull(i)
            val referenceTypeLiteral = types.getOrNull(i)
            if (tableColumnType == referenceTypeLiteral?.id) {
                continue
            }
            val tableColumnLiterals = tableColumnTypes.map(CacheVarLiteral::get)
            val mismatch =
                TypeReferenceResult.DbColumnTypeMismatch(
                    column = column.name,
                    expected = tableColumnLiterals,
                    actual = types,
                    index = i,
                )
            return err(mismatch)
        }

        return ok(FullSuccess)
    }
}
