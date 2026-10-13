package org.runerealm.api.type.refs.resolver

import jakarta.inject.Inject
import org.runerealm.api.type.refs.TypeReferences
import org.runerealm.api.type.refs.area.AreaReferenceResolver
import org.runerealm.api.type.refs.area.AreaReferences
import org.runerealm.api.type.refs.category.CategoryReferenceResolver
import org.runerealm.api.type.refs.category.CategoryReferences
import org.runerealm.api.type.refs.comp.ComponentReferenceResolver
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.content.ContentReferenceResolver
import org.runerealm.api.type.refs.content.ContentReferences
import org.runerealm.api.type.refs.currency.CurrencyReferenceResolver
import org.runerealm.api.type.refs.currency.CurrencyReferences
import org.runerealm.api.type.refs.dbcol.DbColumnReferenceResolver
import org.runerealm.api.type.refs.dbcol.DbColumnReferences
import org.runerealm.api.type.refs.dbrow.DbRowReferenceResolver
import org.runerealm.api.type.refs.dbrow.DbRowReferences
import org.runerealm.api.type.refs.dbtable.DbTableReferenceResolver
import org.runerealm.api.type.refs.dbtable.DbTableReferences
import org.runerealm.api.type.refs.enums.EnumReferenceResolver
import org.runerealm.api.type.refs.enums.EnumReferences
import org.runerealm.api.type.refs.font.FontMetricsReferenceResolver
import org.runerealm.api.type.refs.font.FontMetricsReferences
import org.runerealm.api.type.refs.headbar.HeadbarReferenceResolver
import org.runerealm.api.type.refs.headbar.HeadbarReferences
import org.runerealm.api.type.refs.hitmark.HitmarkReferenceResolver
import org.runerealm.api.type.refs.hitmark.HitmarkReferences
import org.runerealm.api.type.refs.hunt.HuntModeReferenceResolver
import org.runerealm.api.type.refs.hunt.HuntModeReferences
import org.runerealm.api.type.refs.interf.InterfaceReferenceResolver
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.inv.InvReferenceResolver
import org.runerealm.api.type.refs.inv.InvReferences
import org.runerealm.api.type.refs.jingle.JingleReferenceResolver
import org.runerealm.api.type.refs.jingle.JingleReferences
import org.runerealm.api.type.refs.loc.LocReferenceResolver
import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.api.type.refs.midi.MidiReferenceResolver
import org.runerealm.api.type.refs.midi.MidiReferences
import org.runerealm.api.type.refs.mod.ModLevelReferenceResolver
import org.runerealm.api.type.refs.mod.ModLevelReferences
import org.runerealm.api.type.refs.model.ModelReferenceResolver
import org.runerealm.api.type.refs.model.ModelReferences
import org.runerealm.api.type.refs.npc.NpcReferenceResolver
import org.runerealm.api.type.refs.npc.NpcReferences
import org.runerealm.api.type.refs.obj.ObjReferenceResolver
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.param.ParamReferenceResolver
import org.runerealm.api.type.refs.param.ParamReferences
import org.runerealm.api.type.refs.proj.ProjAnimReferenceResolver
import org.runerealm.api.type.refs.proj.ProjAnimReferences
import org.runerealm.api.type.refs.queue.QueueReferenceResolver
import org.runerealm.api.type.refs.queue.QueueReferences
import org.runerealm.api.type.refs.seq.SeqReferenceResolver
import org.runerealm.api.type.refs.seq.SeqReferences
import org.runerealm.api.type.refs.spot.SpotanimReferenceResolver
import org.runerealm.api.type.refs.spot.SpotanimReferences
import org.runerealm.api.type.refs.stat.StatReferenceResolver
import org.runerealm.api.type.refs.stat.StatReferences
import org.runerealm.api.type.refs.struct.StructReferenceResolver
import org.runerealm.api.type.refs.struct.StructReferences
import org.runerealm.api.type.refs.synth.SynthReferenceResolver
import org.runerealm.api.type.refs.synth.SynthReferences
import org.runerealm.api.type.refs.timer.TimerReferenceResolver
import org.runerealm.api.type.refs.timer.TimerReferences
import org.runerealm.api.type.refs.varbit.VarBitReferenceResolver
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.api.type.refs.varn.VarnReferenceResolver
import org.runerealm.api.type.refs.varn.VarnReferences
import org.runerealm.api.type.refs.varnbit.VarnBitReferenceResolver
import org.runerealm.api.type.refs.varnbit.VarnBitReferences
import org.runerealm.api.type.refs.varp.VarpReferenceResolver
import org.runerealm.api.type.refs.varp.VarpReferences
import org.runerealm.api.type.refs.walktrig.WalkTriggerReferenceResolver
import org.runerealm.api.type.refs.walktrig.WalkTriggerReferences

public class TypeReferenceResolverMap
@Inject
constructor(
    private val areaResolver: AreaReferenceResolver,
    private val categoryResolver: CategoryReferenceResolver,
    private val componentResolver: ComponentReferenceResolver,
    private val contentResolver: ContentReferenceResolver,
    private val currencyResolver: CurrencyReferenceResolver,
    private val dbColumnResolver: DbColumnReferenceResolver,
    private val dbRowResolver: DbRowReferenceResolver,
    private val dbTableResolver: DbTableReferenceResolver,
    private val enumResolver: EnumReferenceResolver,
    private val fontMetricsResolver: FontMetricsReferenceResolver,
    private val headbarResolver: HeadbarReferenceResolver,
    private val hitmarkResolver: HitmarkReferenceResolver,
    private val huntResolver: HuntModeReferenceResolver,
    private val interfaceResolver: InterfaceReferenceResolver,
    private val invResolver: InvReferenceResolver,
    private val jingleResolver: JingleReferenceResolver,
    private val locResolver: LocReferenceResolver,
    private val midiResolver: MidiReferenceResolver,
    private val modelResolver: ModelReferenceResolver,
    private val modLevelResolver: ModLevelReferenceResolver,
    private val npcResolver: NpcReferenceResolver,
    private val objResolver: ObjReferenceResolver,
    private val paramResolver: ParamReferenceResolver,
    private val projAnimResolver: ProjAnimReferenceResolver,
    private val queueResolver: QueueReferenceResolver,
    private val seqResolver: SeqReferenceResolver,
    private val spotResolver: SpotanimReferenceResolver,
    private val statResolver: StatReferenceResolver,
    private val structResolver: StructReferenceResolver,
    private val synthResolver: SynthReferenceResolver,
    private val timerResolver: TimerReferenceResolver,
    private val varBitResolver: VarBitReferenceResolver,
    private val varnResolver: VarnReferenceResolver,
    private val varnBitResolver: VarnBitReferenceResolver,
    private val varpResolver: VarpReferenceResolver,
    private val walkTriggerResolver: WalkTriggerReferenceResolver,
) {
    private val references = mutableListOf<TypeReferences<*, *>>()
    private val _errors = mutableListOf<TypeReferenceResult.Error<*>>()
    private val _issues = mutableListOf<TypeReferenceResult.Issue<*>>()
    private val _updates = mutableListOf<TypeReferenceResult.Update<*>>()

    public val size: Int
        get() = references.size

    public val errors: List<TypeReferenceResult.Error<*>>
        get() = _errors

    public val issues: List<TypeReferenceResult.Issue<*>>
        get() = _issues

    public val updates: List<TypeReferenceResult.Update<*>>
        get() = _updates

    public operator fun plusAssign(refs: Collection<TypeReferences<*, *>>) {
        this.references += refs
    }

    public fun resolveAll() {
        for (references in references) {
            resolve(references)
        }
    }

    public fun <T, I> resolve(
        ref: TypeReferences<T, I>,
        res: TypeReferenceResolver<T, I> = ref.resolver(),
    ) {
        val resolved = res.resolve(ref)

        val updates = resolved.filterIsInstance<TypeReferenceResult.Update<*>>()
        _updates += updates

        val issues = resolved.filterIsInstance<TypeReferenceResult.Issue<*>>()
        _issues += issues

        val errors = resolved.filterIsInstance<TypeReferenceResult.Error<*>>()
        _errors += errors
    }

    /**
     * This function can be optionally called to clear stored references after this system is no
     * longer in use.
     */
    public fun clear() {
        references.clear()
        _errors.clear()
        _issues.clear()
        _updates.clear()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T, I> TypeReferences<T, I>.resolver(): TypeReferenceResolver<T, I> {
        val resolver =
            when (this) {
                is AreaReferences -> areaResolver
                is CategoryReferences -> categoryResolver
                is ComponentReferences -> componentResolver
                is ContentReferences -> contentResolver
                is CurrencyReferences -> currencyResolver
                is DbColumnReferences -> dbColumnResolver
                is DbRowReferences -> dbRowResolver
                is DbTableReferences -> dbTableResolver
                is EnumReferences -> enumResolver
                is FontMetricsReferences -> fontMetricsResolver
                is HeadbarReferences -> headbarResolver
                is HitmarkReferences -> hitmarkResolver
                is HuntModeReferences -> huntResolver
                is InterfaceReferences -> interfaceResolver
                is InvReferences -> invResolver
                is JingleReferences -> jingleResolver
                is LocReferences -> locResolver
                is MidiReferences -> midiResolver
                is ModelReferences -> modelResolver
                is ModLevelReferences -> modLevelResolver
                is NpcReferences -> npcResolver
                is ObjReferences -> objResolver
                is ParamReferences -> paramResolver
                is ProjAnimReferences -> projAnimResolver
                is QueueReferences -> queueResolver
                is SeqReferences -> seqResolver
                is SpotanimReferences -> spotResolver
                is StatReferences -> statResolver
                is StructReferences -> structResolver
                is SynthReferences -> synthResolver
                is TimerReferences -> timerResolver
                is VarBitReferences -> varBitResolver
                is VarnReferences -> varnResolver
                is VarnBitReferences -> varnBitResolver
                is VarpReferences -> varpResolver
                is WalkTriggerReferences -> walkTriggerResolver
                else -> throw NotImplementedError("Resolver not defined for type-reference: $this")
            }
        return resolver as TypeReferenceResolver<T, I>
    }
}
