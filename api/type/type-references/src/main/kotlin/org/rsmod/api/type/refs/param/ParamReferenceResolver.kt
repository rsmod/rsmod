package org.rsmod.api.type.refs.param

import jakarta.inject.Inject
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.api.type.refs.resolver.TypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeHashMismatch
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.InvalidImplicitName
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.KeyTypeMismatch
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.NameNotFound
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.issue
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.refs.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.literal.CacheVarTypeMap
import org.rsmod.game.type.literal.CacheVarTypeMap.codecOut
import org.rsmod.game.type.param.HashedParamType
import org.rsmod.game.type.param.ParamTypeList

public class ParamReferenceResolver
@Inject
constructor(
    private val nameMapping: NameMapping,
    private val cacheTypes: TypeListMap,
    private val types: ParamTypeList,
) : TypeReferenceResolver<HashedParamType<*>, Nothing> {
    private val names: Map<String, Int>
        get() = nameMapping.params

    override fun resolve(
        refs: TypeReferences<HashedParamType<*>, Nothing>
    ): List<TypeReferenceResult> = refs.cache.map { it.resolve() }

    @Suppress("UNCHECKED_CAST")
    private fun HashedParamType<*>.resolve(): TypeReferenceResult {
        val name = internalNameGet ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(NameNotFound(name, supposedHash))
        TypeResolver[this] = internalId

        val cacheType = types[internalId] ?: return update(CacheTypeNotFound)

        val cacheTypeLiteral = cacheType.typeLiteral?.codecOut
        val thisTypeLiteral = CacheVarTypeMap.classedLiterals[type]?.codecOut
        if (cacheTypeLiteral != thisTypeLiteral) {
            return err(KeyTypeMismatch(cacheTypeLiteral, thisTypeLiteral))
        }

        val default = cacheType.defaultInt ?: cacheType.defaultStr
        if (default != null) {
            val genericType = this as HashedParamType<Any>
            val typeCodec = CacheVarTypeMap.findCodec<Any, Any>(type)
            val typedDefault = typeCodec.decode(cacheTypes, default)
            TypeResolver.setDefault(genericType, typedDefault)
        }

        val cacheIdentityHash = cacheType.computeIdentityHash()
        if (supposedHash == null) {
            TypeResolver[this] = cacheIdentityHash
            return ok(FullSuccess)
        }
        if (cacheIdentityHash != supposedHash) {
            return issue(CacheTypeHashMismatch(supposedHash, cacheIdentityHash))
        }
        return ok(FullSuccess)
    }
}
