package org.runerealm.api.type.builders.mod

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
import org.runerealm.api.type.script.dsl.ModLevelPluginBuilder
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.mod.ModLevelTypeList
import org.runerealm.game.type.mod.UnpackedModLevelType

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
