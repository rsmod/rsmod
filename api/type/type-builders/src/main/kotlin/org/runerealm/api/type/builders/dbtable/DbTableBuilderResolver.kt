package org.runerealm.api.type.builders.dbtable

import jakarta.inject.Inject
import org.runerealm.api.type.builders.TypeBuilder
import org.runerealm.api.type.builders.resolver.TypeBuilderResolver
import org.runerealm.api.type.builders.resolver.TypeBuilderResult
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.CachePackRequired
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.DbTableColumnMismatch
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.runerealm.api.type.builders.resolver.err
import org.runerealm.api.type.builders.resolver.ok
import org.runerealm.api.type.builders.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.dbtable.DbTableTypeBuilder
import org.runerealm.game.type.dbtable.DbTableTypeList
import org.runerealm.game.type.dbtable.UnpackedDbTableType

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
