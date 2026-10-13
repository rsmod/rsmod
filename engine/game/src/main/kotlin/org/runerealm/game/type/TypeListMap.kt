package org.runerealm.game.type

import org.runerealm.game.type.area.AreaTypeList
import org.runerealm.game.type.category.CategoryTypeList
import org.runerealm.game.type.clientscript.ClientScriptTypeList
import org.runerealm.game.type.comp.ComponentTypeList
import org.runerealm.game.type.dbrow.DbRowTypeList
import org.runerealm.game.type.dbtable.DbTableTypeList
import org.runerealm.game.type.enums.EnumTypeList
import org.runerealm.game.type.font.FontMetricsTypeList
import org.runerealm.game.type.gameval.GameValNameMap
import org.runerealm.game.type.headbar.HeadbarTypeList
import org.runerealm.game.type.hitmark.HitmarkTypeList
import org.runerealm.game.type.hunt.HuntModeTypeList
import org.runerealm.game.type.idk.IdentKitTypeList
import org.runerealm.game.type.interf.InterfaceTypeList
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.jingle.JingleTypeList
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.midi.MidiTypeList
import org.runerealm.game.type.mod.ModLevelTypeList
import org.runerealm.game.type.model.ModelTypeList
import org.runerealm.game.type.npc.NpcTypeList
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.param.ParamTypeList
import org.runerealm.game.type.proj.ProjAnimTypeList
import org.runerealm.game.type.seq.SeqTypeList
import org.runerealm.game.type.spot.SpotanimTypeList
import org.runerealm.game.type.stat.StatTypeList
import org.runerealm.game.type.struct.StructTypeList
import org.runerealm.game.type.synth.SynthTypeList
import org.runerealm.game.type.varbit.VarBitTypeList
import org.runerealm.game.type.varn.VarnTypeList
import org.runerealm.game.type.varnbit.VarnBitTypeList
import org.runerealm.game.type.varp.VarpTypeList
import org.runerealm.game.type.walktrig.WalkTriggerTypeList

public data class TypeListMap(
    public val locs: LocTypeList,
    public val objs: ObjTypeList,
    public val npcs: NpcTypeList,
    public val params: ParamTypeList,
    public val enums: EnumTypeList,
    public val components: ComponentTypeList,
    public val interfaces: InterfaceTypeList,
    public val varps: VarpTypeList,
    public val varbits: VarBitTypeList,
    public val varns: VarnTypeList,
    public val varnbits: VarnBitTypeList,
    public val invs: InvTypeList,
    public val seqs: SeqTypeList,
    public val spotanims: SpotanimTypeList,
    public val fonts: FontMetricsTypeList,
    public val stats: StatTypeList,
    public val synths: SynthTypeList,
    public val structs: StructTypeList,
    public val jingles: JingleTypeList,
    public val walkTriggers: WalkTriggerTypeList,
    public val hitmarks: HitmarkTypeList,
    public val headbars: HeadbarTypeList,
    public val categories: CategoryTypeList,
    public val projanims: ProjAnimTypeList,
    public val midis: MidiTypeList,
    public val gameVals: GameValNameMap,
    public val areas: AreaTypeList,
    public val dbTables: DbTableTypeList,
    public val dbRows: DbRowTypeList,
    public val hunt: HuntModeTypeList,
    public val modLevels: ModLevelTypeList,
    public val clientscripts: ClientScriptTypeList,
    public val models: ModelTypeList,
    public val identKits: IdentKitTypeList,
)
