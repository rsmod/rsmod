package org.runerealm.api.type.editors.npc

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
import org.runerealm.game.type.npc.NpcTypeBuilder
import org.runerealm.game.type.npc.NpcTypeList
import org.runerealm.game.type.npc.UnpackedNpcType

public class NpcEditorResolver
@Inject
constructor(private val types: NpcTypeList, private val nameMapping: NameMapping) :
    TypeEditorResolver<UnpackedNpcType> {
    private val names: Map<String, Int>
        get() = nameMapping.npcs

    override fun resolve(editors: TypeEditor<UnpackedNpcType>): List<TypeEditorResult> =
        editors.cache.map { it.resolve() }

    private fun UnpackedNpcType.resolve(): TypeEditorResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        if (cacheType == null) {
            return err(CacheTypeDoesNotExit)
        }

        val merged = NpcTypeBuilder.merge(this, cacheType)
        if (merged != cacheType) {
            return update(CachePackRequired)
        }

        return ok(FullSuccess)
    }
}
