package org.runerealm.api.type.editors.dbtable

import jakarta.inject.Inject
import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.api.type.editors.resolver.TypeEditorResolver
import org.runerealm.api.type.editors.resolver.TypeEditorResult
import org.runerealm.api.type.editors.resolver.TypeEditorResult.CachePackRequired
import org.runerealm.api.type.editors.resolver.TypeEditorResult.CacheTypeDoesNotExit
import org.runerealm.api.type.editors.resolver.TypeEditorResult.DbTableColumnMismatch
import org.runerealm.api.type.editors.resolver.TypeEditorResult.FullSuccess
import org.runerealm.api.type.editors.resolver.TypeEditorResult.NameNotFound
import org.runerealm.api.type.editors.resolver.err
import org.runerealm.api.type.editors.resolver.ok
import org.runerealm.api.type.editors.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.dbtable.DbTableTypeBuilder
import org.runerealm.game.type.dbtable.DbTableTypeList
import org.runerealm.game.type.dbtable.UnpackedDbTableType

public class DbTableEditorResolver
@Inject
constructor(private val tableTypes: DbTableTypeList, private val nameMapping: NameMapping) :
    TypeEditorResolver<UnpackedDbTableType> {
    private val names: Map<String, Int>
        get() = nameMapping.dbTables

    override fun resolve(editors: TypeEditor<UnpackedDbTableType>): List<TypeEditorResult> =
        editors.cache.map { it.resolve() }

    private fun UnpackedDbTableType.resolve(): TypeEditorResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = tableTypes[internalId]

        TypeResolver[this] = internalId

        val tableMismatch = columnTables.any { it != internalId }
        if (tableMismatch) {
            val actual = columnTables.map { tableTypes[it]?.internalName }
            return err(DbTableColumnMismatch(internalName, actual))
        }

        if (cacheType == null) {
            return err(CacheTypeDoesNotExit)
        }

        val merged = DbTableTypeBuilder.merge(this, cacheType)
        if (merged != cacheType) {
            return update(CachePackRequired)
        }

        return ok(FullSuccess)
    }
}
