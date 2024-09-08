package org.rsmod.api.type.editors.loc

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
import org.rsmod.api.type.script.dsl.LocPluginBuilder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.loc.UnpackedLocType

public class LocEditorResolver
@Inject
constructor(private val types: LocTypeList, private val nameMapping: NameMapping) :
    TypeEditorResolver<LocPluginBuilder, UnpackedLocType> {
    private val names: Map<String, Int>
        get() = nameMapping.locs

    override fun resolve(
        editors: TypeEditor<LocPluginBuilder, UnpackedLocType>
    ): List<TypeEditorResult> = editors.cache.map { it.resolve() }

    private fun UnpackedLocType.resolve(): TypeEditorResult {
        val internalId = names[internalNameGet] ?: return err(NameNotFound(internalNameGet))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        if (cacheType == null) {
            return err(CacheTypeDoesNotExit)
        }

        val merged = LocTypeBuilder.merge(this, cacheType)
        if (merged != cacheType) {
            return update(CachePackRequired)
        }

        return ok(FullSuccess)
    }
}
