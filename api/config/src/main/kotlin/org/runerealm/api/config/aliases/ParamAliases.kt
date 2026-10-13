package org.runerealm.api.config.aliases

import org.runerealm.game.type.category.CategoryType
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.headbar.HeadbarType
import org.runerealm.game.type.hitmark.HitmarkType
import org.runerealm.game.type.loc.LocType
import org.runerealm.game.type.npc.NpcType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.game.type.param.ParamType
import org.runerealm.game.type.proj.ProjAnimType
import org.runerealm.game.type.seq.SeqType
import org.runerealm.game.type.spot.SpotanimType
import org.runerealm.game.type.stat.StatType
import org.runerealm.game.type.synth.SynthType
import org.runerealm.game.type.varbit.VarBitType
import org.runerealm.game.type.varp.VarpType
import org.runerealm.map.CoordGrid

typealias ParamInt = ParamType<Int>

typealias ParamStr = ParamType<String>

typealias ParamBool = ParamType<Boolean>

typealias ParamCategory = ParamType<CategoryType>

typealias ParamComponent = ParamType<ComponentType>

typealias ParamCoord = ParamType<CoordGrid>

typealias ParamHeadbar = ParamType<HeadbarType>

typealias ParamHitmark = ParamType<HitmarkType>

typealias ParamObj = ParamType<ObjType>

typealias ParamNamedObj = ParamType<UnpackedObjType>

typealias ParamLoc = ParamType<LocType>

typealias ParamNpc = ParamType<NpcType>

typealias ParamProj = ParamType<ProjAnimType>

typealias ParamSeq = ParamType<SeqType>

typealias ParamSpot = ParamType<SpotanimType>

typealias ParamStat = ParamType<StatType>

typealias ParamSynth = ParamType<SynthType>

typealias ParamVarBit = ParamType<VarBitType>

typealias ParamVarp = ParamType<VarpType>
