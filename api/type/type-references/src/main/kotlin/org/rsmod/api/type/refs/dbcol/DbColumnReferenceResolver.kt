package org.rsmod.api.type.refs.dbcol

import jakarta.inject.Inject
import kotlin.math.max
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.api.type.refs.resolver.TypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.ImplicitNameNotFound
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.refs.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.dbtable.DbTableTypeList
import org.rsmod.game.type.literal.CacheVarLiteral

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
