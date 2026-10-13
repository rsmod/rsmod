package org.runerealm.api.type.editors.inv

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
import org.runerealm.game.type.inv.InvTypeBuilder
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.inv.UnpackedInvType

public class InvEditorResolver
@Inject
constructor(private val types: InvTypeList, private val nameMapping: NameMapping) :
    TypeEditorResolver<UnpackedInvType> {
    private val names: Map<String, Int>
        get() = nameMapping.invs

    override fun resolve(editors: TypeEditor<UnpackedInvType>): List<TypeEditorResult> =
        editors.cache.map { it.resolve() }

    private fun UnpackedInvType.resolve(): TypeEditorResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        if (cacheType == null) {
            return err(CacheTypeDoesNotExit)
        }

        val merged = InvTypeBuilder.merge(this, cacheType)
        if (merged != cacheType) {
            return update(CachePackRequired)
        }

        return ok(FullSuccess)
    }
}
