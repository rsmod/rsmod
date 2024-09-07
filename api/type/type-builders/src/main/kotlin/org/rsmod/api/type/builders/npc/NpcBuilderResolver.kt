package org.rsmod.api.type.builders.npc

import jakarta.inject.Inject
import kotlin.collections.get
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
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType

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
        val internalId = names[internalNameGet] ?: return err(NameNotFound(internalNameGet))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        return if (cacheType != this) {
            update(CachePackRequired)
        } else {
            ok(FullSuccess)
        }
    }
}
