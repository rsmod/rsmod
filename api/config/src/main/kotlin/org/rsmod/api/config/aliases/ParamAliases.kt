package org.rsmod.api.config.aliases

import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.map.CoordGrid

typealias ParamInt = ParamType<Int>

typealias ParamStr = ParamType<String>

typealias ParamBool = ParamType<Boolean>

typealias ParamCoord = ParamType<CoordGrid>

typealias ParamObj = ParamType<ObjType>

typealias ParamNamedObj = ParamType<UnpackedObjType>

typealias ParamLoc = ParamType<LocType>

typealias ParamNpc = ParamType<NpcType>

typealias ParamSeq = ParamType<SeqType>

typealias ParamSpot = ParamType<SpotanimType>

typealias ParamStat = ParamType<StatType>

typealias ParamSynth = ParamType<SynthType>

typealias ParamVarBit = ParamType<VarBitType>

typealias ParamVarp = ParamType<VarpType>
