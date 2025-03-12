package org.rsmod.api.type.builders.obj

import jakarta.inject.Inject
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
import org.rsmod.game.type.obj.ObjTypeBuilder
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

public class ObjBuilderResolver
@Inject
constructor(private val types: ObjTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<ObjTypeBuilder, UnpackedObjType> {
    private val names: Map<String, Int>
        get() = nameMapping.objs

    override fun resolve(
        builders: TypeBuilder<ObjTypeBuilder, UnpackedObjType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedObjType.resolve(): TypeBuilderResult {
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
