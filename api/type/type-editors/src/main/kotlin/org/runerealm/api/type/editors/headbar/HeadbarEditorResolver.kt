package org.runerealm.api.type.editors.headbar

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
import org.runerealm.game.type.headbar.HeadbarTypeBuilder
import org.runerealm.game.type.headbar.HeadbarTypeList
import org.runerealm.game.type.headbar.UnpackedHeadbarType

public class HeadbarEditorResolver
@Inject
constructor(private val types: HeadbarTypeList, private val nameMapping: NameMapping) :
    TypeEditorResolver<UnpackedHeadbarType> {
    private val names: Map<String, Int>
        get() = nameMapping.headbars

    override fun resolve(editors: TypeEditor<UnpackedHeadbarType>): List<TypeEditorResult> =
        editors.cache.map { it.resolve() }

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
