package org.rsmod.api.type.builders.varbit

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
import org.rsmod.game.type.varbit.UnpackedVarBitType
import org.rsmod.game.type.varbit.VarBitTypeBuilder
import org.rsmod.game.type.varbit.VarBitTypeList

public class VarBitBuilderResolver
@Inject
constructor(private val types: VarBitTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<VarBitTypeBuilder, UnpackedVarBitType> {
    private val names: Map<String, Int>
        get() = nameMapping.varbits

    override fun resolve(
        builders: TypeBuilder<VarBitTypeBuilder, UnpackedVarBitType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedVarBitType.resolve(): TypeBuilderResult {
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
