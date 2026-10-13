package org.runerealm.api.type.editors.varp

import jakarta.inject.Inject
import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.api.type.editors.resolver.TypeEditorResolver
import org.runerealm.api.type.editors.resolver.TypeEditorResult
import org.runerealm.api.type.editors.resolver.TypeEditorResult.CachePackRequired
import org.runerealm.api.type.editors.resolver.TypeEditorResult.CacheTypeDoesNotExit
import org.runerealm.api.type.editors.resolver.TypeEditorResult.FullSuccess
import org.runerealm.api.type.editors.resolver.TypeEditorResult.NameNotFound
import org.runerealm.api.type.editors.resolver.err
import org.runerealm.api.type.editors.resolver.ok
import org.runerealm.api.type.editors.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.varp.UnpackedVarpType
import org.runerealm.game.type.varp.VarpTypeBuilder
import org.runerealm.game.type.varp.VarpTypeList

public class VarpEditorResolver
@Inject
constructor(private val types: VarpTypeList, private val nameMapping: NameMapping) :
    TypeEditorResolver<UnpackedVarpType> {
    private val names: Map<String, Int>
        get() = nameMapping.varps

    override fun resolve(editors: TypeEditor<UnpackedVarpType>): List<TypeEditorResult> =
        editors.cache.map { it.resolve() }

    private fun UnpackedVarpType.resolve(): TypeEditorResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        if (cacheType == null) {
            return err(CacheTypeDoesNotExit)
        }

        val merged = VarpTypeBuilder.merge(this, cacheType)
        if (merged != cacheType) {
            return update(CachePackRequired)
        }

        return ok(FullSuccess)
    }
}
