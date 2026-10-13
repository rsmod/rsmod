package org.runerealm.api.type.builders.inv

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
import org.runerealm.api.type.script.dsl.InvPluginBuilder
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.inv.UnpackedInvType

public class InvBuilderResolver
@Inject
constructor(private val types: InvTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<InvPluginBuilder, UnpackedInvType> {
    private val names: Map<String, Int>
        get() = nameMapping.invs

    override fun resolve(
        builders: TypeBuilder<InvPluginBuilder, UnpackedInvType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedInvType.resolve(): TypeBuilderResult {
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
