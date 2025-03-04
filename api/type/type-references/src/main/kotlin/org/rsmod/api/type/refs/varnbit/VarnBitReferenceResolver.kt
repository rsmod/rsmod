package org.rsmod.api.type.refs.varnbit

import jakarta.inject.Inject
import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.api.type.refs.resolver.NameTypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.ImplicitNameNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.InvalidImplicitName
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.refs.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.varnbit.HashedVarnBitType
import org.rsmod.game.type.varnbit.VarnBitTypeList

public class VarnBitReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: VarnBitTypeList) :
    NameTypeReferenceResolver<HashedVarnBitType> {
    private val names: Map<String, Int>
        get() = nameMapping.varnbits

    override fun resolve(refs: NameTypeReferences<HashedVarnBitType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun HashedVarnBitType.resolve(): TypeReferenceResult {
        val name = internalName ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(ImplicitNameNotFound(name))
        TypeResolver[this] = internalId

        val cacheType = types[internalId] ?: return update(CacheTypeNotFound)
        TypeResolver.setBits(this, cacheType.bits)
        TypeResolver.setBaseVar(this, cacheType.baseVar)

        return ok(FullSuccess)
    }
}
