package org.rsmod.api.type.builders.mod

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
import org.rsmod.api.type.script.dsl.ModLevelPluginBuilder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.mod.ModLevelTypeList
import org.rsmod.game.type.mod.UnpackedModLevelType

public class ModLevelBuilderResolver
@Inject
constructor(private val types: ModLevelTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<ModLevelPluginBuilder, UnpackedModLevelType> {
    private val names: Map<String, Int>
        get() = nameMapping.modLevels

    override fun resolve(
        builders: TypeBuilder<ModLevelPluginBuilder, UnpackedModLevelType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedModLevelType.resolve(): TypeBuilderResult {
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
