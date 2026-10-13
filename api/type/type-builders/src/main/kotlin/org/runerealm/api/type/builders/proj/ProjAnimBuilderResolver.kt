package org.runerealm.api.type.builders.proj

import jakarta.inject.Inject
import org.runerealm.api.type.builders.TypeBuilder
import org.runerealm.api.type.builders.resolver.TypeBuilderResolver
import org.runerealm.api.type.builders.resolver.TypeBuilderResult
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.CachePackRequired
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.runerealm.api.type.builders.resolver.err
import org.runerealm.api.type.builders.resolver.ok
import org.runerealm.api.type.builders.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.proj.ProjAnimTypeBuilder
import org.runerealm.game.type.proj.ProjAnimTypeList
import org.runerealm.game.type.proj.UnpackedProjAnimType

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
