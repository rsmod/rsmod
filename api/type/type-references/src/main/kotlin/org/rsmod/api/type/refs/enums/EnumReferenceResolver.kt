package org.rsmod.api.type.refs.enums

import jakarta.inject.Inject
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.api.type.refs.resolver.TypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeHashMismatch
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.HashNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.KeyTypeMismatch
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.KeyValTypeMismatch
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.NameNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.ValTypeMismatch
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.issue
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.refs.resolver.update
import org.rsmod.api.type.symbols.hash.HashMapping
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.enums.HashedEnumType
import org.rsmod.game.type.literal.CacheVarTypeMap
import org.rsmod.game.type.literal.CacheVarTypeMap.codecOut

public class EnumReferenceResolver
@Inject
constructor(
    private val nameMapping: NameMapping,
    private val hashMapping: HashMapping,
    private val types: EnumTypeList,
) : TypeReferenceResolver<HashedEnumType<*, *>, Nothing> {
    private val names: Map<String, Int>
        get() = nameMapping.enums

    private val hashes: Map<Long, String>
        get() = hashMapping.enums

    override fun resolve(
        refs: TypeReferences<HashedEnumType<*, *>, Nothing>
    ): List<TypeReferenceResult> = refs.cache.map { it.resolve() }

    @Suppress("UNCHECKED_CAST")
    private fun HashedEnumType<*, *>.resolve(): TypeReferenceResult {
        val name = hashes[supposedHash] ?: return err(HashNotFound(supposedHash))
        val internalId = names[name] ?: return err(NameNotFound(name, supposedHash))
        val cacheType = types[internalId]

        TypeResolver[this] = name
        TypeResolver[this] = internalId

        if (cacheType == null) {
            return update(CacheTypeNotFound)
        }

        val cacheKeyLiteral = cacheType.keyLiteral.codecOut
        val thisKeyLiteral = CacheVarTypeMap.classedLiterals[keyType]?.codecOut
        val keyTypeMismatch = cacheKeyLiteral != thisKeyLiteral

        val cacheValLiteral = cacheType.valLiteral.codecOut
        val thisValLiteral = CacheVarTypeMap.classedLiterals[valType]?.codecOut
        val valTypeMismatch = cacheValLiteral != thisValLiteral

        if (keyTypeMismatch && valTypeMismatch) {
            val mismatch =
                KeyValTypeMismatch(cacheKeyLiteral, thisKeyLiteral, cacheValLiteral, thisValLiteral)
            return err(mismatch)
        }

        if (keyTypeMismatch) {
            return err(KeyTypeMismatch(cacheKeyLiteral, thisKeyLiteral))
        }

        if (valTypeMismatch) {
            return err(ValTypeMismatch(cacheValLiteral, thisValLiteral))
        }

        val cacheHash = cacheType.computeIdentityHash()
        return if (cacheHash != supposedHash) {
            issue(CacheTypeHashMismatch(supposedHash, cacheHash))
        } else {
            ok(FullSuccess)
        }
    }
}
