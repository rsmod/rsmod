package org.rsmod.api.type.refs.walktrig

import jakarta.inject.Inject
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.api.type.refs.resolver.TypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.NameNotFound
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.refs.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.type.walktrig.WalkTriggerTypeList

public class WalkTriggerReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: WalkTriggerTypeList) :
    TypeReferenceResolver<WalkTriggerType, Nothing> {
    private val names: Map<String, Int>
        get() = nameMapping.walkTriggers

    override fun resolve(
        refs: TypeReferences<WalkTriggerType, Nothing>
    ): List<TypeReferenceResult> = refs.cache.map { it.resolve() }

    private fun WalkTriggerType.resolve(): TypeReferenceResult {
        val name = internalNameGet
        val internalId = names[name] ?: return err(NameNotFound(name, null))
        TypeResolver[this] = internalId

        val cacheType = types[internalId] ?: return update(CacheTypeNotFound)
        TypeResolver[this] = cacheType.priority

        return ok(FullSuccess)
    }
}
