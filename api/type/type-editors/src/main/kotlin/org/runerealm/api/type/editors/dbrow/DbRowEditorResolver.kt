package org.runerealm.api.type.editors.dbrow

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
import org.runerealm.game.type.dbrow.DbRowTypeBuilder
import org.runerealm.game.type.dbrow.DbRowTypeList
import org.runerealm.game.type.dbrow.UnpackedDbRowType

public class DbRowEditorResolver
@Inject
constructor(private val rowTypes: DbRowTypeList, private val nameMapping: NameMapping) :
    TypeEditorResolver<UnpackedDbRowType> {
    private val names: Map<String, Int>
        get() = nameMapping.dbRows

    override fun resolve(editors: TypeEditor<UnpackedDbRowType>): List<TypeEditorResult> =
        editors.cache.map { it.resolve() }

    private fun UnpackedDbRowType.resolve(): TypeEditorResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = rowTypes[internalId]

        TypeResolver[this] = internalId

        if (cacheType == null) {
            return err(CacheTypeDoesNotExit)
        }

        val merged = DbRowTypeBuilder.merge(this, cacheType)
        if (merged != cacheType) {
            return update(CachePackRequired)
        }

        return ok(FullSuccess)
    }
}
