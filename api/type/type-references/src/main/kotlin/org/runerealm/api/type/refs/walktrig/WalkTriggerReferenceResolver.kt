package org.runerealm.api.type.refs.walktrig

import jakarta.inject.Inject
import org.runerealm.api.type.refs.TypeReferences
import org.runerealm.api.type.refs.resolver.TypeReferenceResolver
import org.runerealm.api.type.refs.resolver.TypeReferenceResult
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.InvalidImplicitName
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.NameNotFound
import org.runerealm.api.type.refs.resolver.err
import org.runerealm.api.type.refs.resolver.ok
import org.runerealm.api.type.refs.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.walktrig.WalkTriggerType
import org.runerealm.game.type.walktrig.WalkTriggerTypeList

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
        val name = internalName ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(NameNotFound(name, null))
        TypeResolver[this] = internalId

        val cacheType = types[internalId] ?: return update(CacheTypeNotFound)
        TypeResolver[this] = cacheType.priority

        return ok(FullSuccess)
    }
}
