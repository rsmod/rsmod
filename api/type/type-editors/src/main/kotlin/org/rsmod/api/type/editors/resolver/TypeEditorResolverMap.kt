package org.rsmod.api.type.editors.resolver

import jakarta.inject.Inject
import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.editors.inv.InvEditorResolver
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.editors.loc.LocEditorResolver
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.editors.npc.NpcEditorResolver
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.editors.obj.ObjEditorResolver
import org.rsmod.api.type.editors.struct.StructEditor
import org.rsmod.api.type.editors.struct.StructEditorResolver
import org.rsmod.api.type.editors.varp.VarpEditor
import org.rsmod.api.type.editors.varp.VarpEditorResolver

public class TypeEditorResolverMap
@Inject
constructor(
    private val invResolver: InvEditorResolver,
    private val locResolver: LocEditorResolver,
    private val npcResolver: NpcEditorResolver,
    private val objResolver: ObjEditorResolver,
    private val structResolver: StructEditorResolver,
    private val varpResolver: VarpEditorResolver,
) {
    private val editors = mutableListOf<TypeEditor<*, *>>()

    private val _resultValues = mutableListOf<Any>()
    private val _errors = mutableListOf<TypeEditorResult.Error<*>>()
    private val _updates = mutableListOf<TypeEditorResult.Update<*>>()

    public val size: Int
        get() = editors.size

    public val resultValues: List<Any>
        get() = _resultValues

    public val errors: List<TypeEditorResult.Error<*>>
        get() = _errors

    public val updates: List<TypeEditorResult.Update<*>>
        get() = _updates

    public operator fun plusAssign(editors: Collection<TypeEditor<*, *>>) {
        this.editors += editors
    }

    public fun resolveAll() {
        for (editors in editors) {
            resolve(editors)
        }
    }

    public fun <B, T> resolve(
        editors: TypeEditor<B, T>,
        res: TypeEditorResolver<B, T> = editors.resolver(),
    ) {
        val resolved = res.resolve(editors)

        val updates = resolved.filterIsInstance<TypeEditorResult.Update<Any>>()
        _updates += updates

        val errors = resolved.filterIsInstance<TypeEditorResult.Error<Any>>()
        _errors += errors

        val success = resolved.filterIsInstance<TypeEditorResult.Success<Any>>()
        val results = updates.map { it.value } + errors.map { it.value } + success.map { it.value }
        _resultValues.addAll(results)
    }

    /**
     * This function can be optionally called to clear stored references after this system is no
     * longer in use.
     */
    public fun clear() {
        editors.clear()
        _errors.clear()
        _updates.clear()
        _resultValues.clear()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <B, T> TypeEditor<B, T>.resolver(): TypeEditorResolver<B, T> {
        val resolver =
            when (this) {
                is InvEditor -> invResolver
                is LocEditor -> locResolver
                is NpcEditor -> npcResolver
                is ObjEditor -> objResolver
                is StructEditor -> structResolver
                is VarpEditor -> varpResolver
                else -> throw NotImplementedError("Resolver not defined for type-editor: $this")
            }
        return resolver as TypeEditorResolver<B, T>
    }
}
