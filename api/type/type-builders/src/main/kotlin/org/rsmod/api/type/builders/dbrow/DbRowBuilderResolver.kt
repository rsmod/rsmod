package org.rsmod.api.type.builders.dbrow

import jakarta.inject.Inject
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.resolver.TypeBuilderResolver
import org.rsmod.api.type.builders.resolver.TypeBuilderResult
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.CachePackRequired
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.rsmod.api.type.builders.resolver.err
import org.rsmod.api.type.builders.resolver.ok
import org.rsmod.api.type.builders.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.dbrow.DbRowTypeBuilder
import org.rsmod.game.type.dbrow.DbRowTypeList
import org.rsmod.game.type.dbrow.UnpackedDbRowType

public class DbRowBuilderResolver
@Inject
constructor(private val rowTypes: DbRowTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<DbRowTypeBuilder, UnpackedDbRowType> {
    private val names: Map<String, Int>
        get() = nameMapping.dbRows

    override fun resolve(
        builders: TypeBuilder<DbRowTypeBuilder, UnpackedDbRowType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedDbRowType.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = rowTypes[internalId]

        TypeResolver[this] = internalId

        return if (cacheType != this) {
            update(CachePackRequired)
        } else {
            ok(FullSuccess)
        }
    }
}
