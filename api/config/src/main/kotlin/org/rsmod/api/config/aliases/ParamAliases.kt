package org.rsmod.api.config.aliases

import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.map.CoordGrid

public typealias ParamInt = ParamType<Int>

public typealias ParamStr = ParamType<String>

public typealias ParamBool = ParamType<Boolean>

public typealias ParamCoord = ParamType<CoordGrid>

public typealias ParamObj = ParamType<ObjType>

public typealias ParamNamedObj = ParamType<UnpackedObjType>

public typealias ParamLoc = ParamType<LocType>

public typealias ParamSeq = ParamType<SeqType>

public typealias ParamSynth = ParamType<SynthType>
