package org.runerealm.api.type.builders.npc

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
import org.runerealm.game.type.npc.NpcTypeBuilder
import org.runerealm.game.type.npc.NpcTypeList
import org.runerealm.game.type.npc.UnpackedNpcType

public class NpcBuilderResolver
@Inject
constructor(private val types: NpcTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<NpcTypeBuilder, UnpackedNpcType> {
    private val names: Map<String, Int>
        get() = nameMapping.npcs

    override fun resolve(
        builders: TypeBuilder<NpcTypeBuilder, UnpackedNpcType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedNpcType.resolve(): TypeBuilderResult {
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
