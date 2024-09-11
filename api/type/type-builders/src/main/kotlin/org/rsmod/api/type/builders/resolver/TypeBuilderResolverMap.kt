package org.rsmod.api.type.builders.resolver

import jakarta.inject.Inject
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.builders.enums.EnumBuilderResolver
import org.rsmod.api.type.builders.inv.InvBuilder
import org.rsmod.api.type.builders.inv.InvBuilderResolver
import org.rsmod.api.type.builders.loc.LocBuilder
import org.rsmod.api.type.builders.loc.LocBuilderResolver
import org.rsmod.api.type.builders.mesanim.MesAnimBuilder
import org.rsmod.api.type.builders.mesanim.MesAnimResolver
import org.rsmod.api.type.builders.mod.ModGroupBuilder
import org.rsmod.api.type.builders.mod.ModGroupResolver
import org.rsmod.api.type.builders.npc.NpcBuilder
import org.rsmod.api.type.builders.npc.NpcBuilderResolver
import org.rsmod.api.type.builders.obj.ObjBuilder
import org.rsmod.api.type.builders.obj.ObjBuilderResolver
import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.api.type.builders.param.ParamBuilderResolver
import org.rsmod.api.type.builders.varobjbit.VarObjBitBuilder
import org.rsmod.api.type.builders.varobjbit.VarObjBitResolver

public class TypeBuilderResolverMap
@Inject
constructor(
    private val enumResolver: EnumBuilderResolver,
    private val invResolver: InvBuilderResolver,
    private val locResolver: LocBuilderResolver,
    private val mesAnimResolver: MesAnimResolver,
    private val modGroupResolver: ModGroupResolver,
    private val npcResolver: NpcBuilderResolver,
    private val objResolver: ObjBuilderResolver,
    private val paramResolver: ParamBuilderResolver,
    private val varObjBitResolver: VarObjBitResolver,
) {
    private val builders = mutableListOf<TypeBuilder<*, *>>()

    private val _resultValues = mutableListOf<Any>()
    private val _errors = mutableListOf<TypeBuilderResult.Error<*>>()
    private val _updates = mutableListOf<TypeBuilderResult.Update<*>>()

    public val size: Int
        get() = builders.size

    public val resultValues: List<Any>
        get() = _resultValues

    public val errors: List<TypeBuilderResult.Error<*>>
        get() = _errors

    public val updates: List<TypeBuilderResult.Update<*>>
        get() = _updates

    public operator fun plusAssign(builders: Collection<TypeBuilder<*, *>>) {
        this.builders += builders
    }

    public fun resolveAll() {
        for (builders in builders) {
            resolve(builders)
        }
    }

    public fun <B, T> resolve(
        builders: TypeBuilder<B, T>,
        res: TypeBuilderResolver<B, T> = builders.resolver(),
    ) {
        val resolved = res.resolve(builders)

        val updates = resolved.filterIsInstance<TypeBuilderResult.Update<Any>>()
        _updates += updates

        val errors = resolved.filterIsInstance<TypeBuilderResult.Error<Any>>()
        _errors += errors

        val success = resolved.filterIsInstance<TypeBuilderResult.Success<Any>>()
        val results = updates.map { it.value } + errors.map { it.value } + success.map { it.value }
        _resultValues.addAll(results)
    }

    /**
     * This function can be optionally called to clear stored references after this system is no
     * longer in use.
     */
    public fun clear() {
        builders.clear()
        _errors.clear()
        _updates.clear()
        _resultValues.clear()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <B, T> TypeBuilder<B, T>.resolver(): TypeBuilderResolver<B, T> {
        val resolver =
            when (this) {
                is EnumBuilder -> enumResolver
                is InvBuilder -> invResolver
                is LocBuilder -> locResolver
                is MesAnimBuilder -> mesAnimResolver
                is ModGroupBuilder -> modGroupResolver
                is NpcBuilder -> npcResolver
                is ObjBuilder -> objResolver
                is ParamBuilder -> paramResolver
                is VarObjBitBuilder -> varObjBitResolver
                else -> throw NotImplementedError("Resolver not defined for type-builder: $this")
            }
        return resolver as TypeBuilderResolver<B, T>
    }
}
