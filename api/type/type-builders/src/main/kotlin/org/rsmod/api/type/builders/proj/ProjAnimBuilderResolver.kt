package org.rsmod.api.type.builders.proj

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
import org.rsmod.game.type.proj.ProjAnimTypeBuilder
import org.rsmod.game.type.proj.ProjAnimTypeList
import org.rsmod.game.type.proj.UnpackedProjAnimType

public class ProjAnimBuilderResolver
@Inject
constructor(private val types: ProjAnimTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<ProjAnimTypeBuilder, UnpackedProjAnimType> {
    private val names: Map<String, Int>
        get() = nameMapping.projanims

    override fun resolve(
        builders: TypeBuilder<ProjAnimTypeBuilder, UnpackedProjAnimType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedProjAnimType.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        return if (cacheType != this) {
            update(CachePackRequired)
        } else {
            ok(FullSuccess)
        }
    }
}
