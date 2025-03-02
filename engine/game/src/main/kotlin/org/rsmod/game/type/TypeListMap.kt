package org.rsmod.game.type

import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.font.FontMetricsTypeList
import org.rsmod.game.type.hitmark.HitmarkTypeList
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.jingle.JingleTypeList
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.param.ParamTypeList
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.spot.SpotanimTypeList
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.game.type.struct.StructTypeList
import org.rsmod.game.type.synth.SynthTypeList
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varn.VarnTypeList
import org.rsmod.game.type.varnbit.VarnBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.type.walktrig.WalkTriggerTypeList

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
)
