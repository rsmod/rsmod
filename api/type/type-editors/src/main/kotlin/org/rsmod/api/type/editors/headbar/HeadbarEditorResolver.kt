package org.rsmod.api.type.editors.headbar

import jakarta.inject.Inject
import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.editors.resolver.TypeEditorResolver
import org.rsmod.api.type.editors.resolver.TypeEditorResult
import org.rsmod.api.type.editors.resolver.TypeEditorResult.CachePackRequired
import org.rsmod.api.type.editors.resolver.TypeEditorResult.CacheTypeDoesNotExit
import org.rsmod.api.type.editors.resolver.TypeEditorResult.FullSuccess
import org.rsmod.api.type.editors.resolver.TypeEditorResult.NameNotFound
import org.rsmod.api.type.editors.resolver.err
import org.rsmod.api.type.editors.resolver.ok
import org.rsmod.api.type.editors.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.headbar.HeadbarTypeBuilder
import org.rsmod.game.type.headbar.HeadbarTypeList
import org.rsmod.game.type.headbar.UnpackedHeadbarType

public class HeadbarEditorResolver
@Inject
constructor(private val types: HeadbarTypeList, private val nameMapping: NameMapping) :
    TypeEditorResolver<HeadbarTypeBuilder, UnpackedHeadbarType> {
    private val names: Map<String, Int>
        get() = nameMapping.headbars

    override fun resolve(
        editors: TypeEditor<HeadbarTypeBuilder, UnpackedHeadbarType>
    ): List<TypeEditorResult> = editors.cache.map { it.resolve() }

    private fun UnpackedHeadbarType.resolve(): TypeEditorResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        if (cacheType == null) {
            return err(CacheTypeDoesNotExit)
        }

        val merged = HeadbarTypeBuilder.merge(this, cacheType)
        if (merged != cacheType) {
            return update(CachePackRequired)
        }

        return ok(FullSuccess)
    }
}
