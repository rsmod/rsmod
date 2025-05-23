package org.rsmod.api.type.builders.dbtable

import jakarta.inject.Inject
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.resolver.TypeBuilderResolver
import org.rsmod.api.type.builders.resolver.TypeBuilderResult
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.CachePackRequired
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.DbTableColumnMismatch
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.rsmod.api.type.builders.resolver.err
import org.rsmod.api.type.builders.resolver.ok
import org.rsmod.api.type.builders.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.dbtable.DbTableTypeBuilder
import org.rsmod.game.type.dbtable.DbTableTypeList
import org.rsmod.game.type.dbtable.UnpackedDbTableType

public class DbTableBuilderResolver
@Inject
constructor(private val tableTypes: DbTableTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<DbTableTypeBuilder, UnpackedDbTableType> {
    private val names: Map<String, Int>
        get() = nameMapping.dbTables

    override fun resolve(
        builders: TypeBuilder<DbTableTypeBuilder, UnpackedDbTableType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedDbTableType.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = tableTypes[internalId]

        TypeResolver[this] = internalId

        val tableMismatch = columnTables.any { it != internalId }
        if (tableMismatch) {
            val actual = columnTables.map { tableTypes[it]?.internalName }
            return err(DbTableColumnMismatch(internalName, actual))
        }

        return if (cacheType != this) {
            update(CachePackRequired)
        } else {
            ok(FullSuccess)
        }
    }
}
