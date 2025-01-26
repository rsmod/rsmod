package org.rsmod.game.type.literal

import kotlin.reflect.KClass
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.comp.HashedComponentType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.enums.HashedEnumType
import org.rsmod.game.type.loc.HashedLocType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.npc.HashedNpcType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.HashedObjType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.HashedSeqType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.HashedSpotanimType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varbit.HashedVarBitType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.HashedVarpType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.map.CoordGrid

public object CacheVarTypeMap {
    public val classedLiterals: Map<KClass<*>, CacheVarLiteral> =
        hashMapOf(
            Boolean::class to CacheVarLiteral.BOOL,
            ComponentType::class to CacheVarLiteral.COMPONENT,
            HashedComponentType::class to CacheVarLiteral.COMPONENT,
            Int::class to CacheVarLiteral.INT,
            CoordGrid::class to CacheVarLiteral.COORDGRID,
            EnumType::class to CacheVarLiteral.ENUM,
            HashedEnumType::class to CacheVarLiteral.ENUM,
            LocType::class to CacheVarLiteral.LOC,
            HashedLocType::class to CacheVarLiteral.LOC,
            NpcType::class to CacheVarLiteral.NPC,
            HashedNpcType::class to CacheVarLiteral.NPC,
            ObjType::class to CacheVarLiteral.OBJ,
            HashedObjType::class to CacheVarLiteral.OBJ,
            SeqType::class to CacheVarLiteral.SEQ,
            HashedSeqType::class to CacheVarLiteral.SEQ,
            SpotanimType::class to CacheVarLiteral.SPOTANIM,
            HashedSpotanimType::class to CacheVarLiteral.SPOTANIM,
            UnpackedObjType::class to CacheVarLiteral.NAMEDOBJ,
            String::class to CacheVarLiteral.STRING,
            StatType::class to CacheVarLiteral.STAT,
            SynthType::class to CacheVarLiteral.SYNTH,
            VarBitType::class to CacheVarLiteral.VARBIT,
            HashedVarBitType::class to CacheVarLiteral.VARBIT,
            VarpType::class to CacheVarLiteral.VARP,
            HashedVarpType::class to CacheVarLiteral.VARP,
        )

    public val codecMap: Map<KClass<*>, CacheVarCodec<*, *>> =
        hashMapOf(
            Boolean::class to CacheVarBoolCodec,
            ComponentType::class to CacheVarComponentCodec,
            HashedComponentType::class to CacheVarComponentCodec,
            Int::class to CacheVarIntCodec,
            CoordGrid::class to CacheVarCoordGridCodec,
            EnumType::class to CacheVarEnumCodec,
            HashedEnumType::class to CacheVarEnumCodec,
            LocType::class to CacheVarLocCodec,
            HashedLocType::class to CacheVarLocCodec,
            NpcType::class to CacheVarNpcCodec,
            HashedNpcType::class to CacheVarNpcCodec,
            ObjType::class to CacheVarObjCodec,
            HashedObjType::class to CacheVarObjCodec,
            SeqType::class to CacheVarSeqCodec,
            HashedSeqType::class to CacheVarSeqCodec,
            SpotanimType::class to CacheVarSpotanimCodec,
            HashedSpotanimType::class to CacheVarSpotanimCodec,
            UnpackedObjType::class to CacheVarNamedObjCodec,
            String::class to CacheVarStringCodec,
            StatType::class to CacheVarStatCodec,
            SynthType::class to CacheVarSynthCodec,
            VarBitType::class to CacheVarVarBitCodec,
            HashedVarBitType::class to CacheVarVarBitCodec,
            VarpType::class to CacheVarVarpCodec,
            HashedVarpType::class to CacheVarVarpCodec,
        )

    public val CacheVarLiteral.codecOut: KClass<*>
        get() =
            when (this) {
                CacheVarLiteral.BOOL -> Boolean::class
                CacheVarLiteral.ENTITY_OVERLAY -> Int::class
                CacheVarLiteral.SEQ -> SeqType::class
                CacheVarLiteral.COLOUR -> Int::class
                CacheVarLiteral.TOPLEVEL_INTERFACE -> Int::class
                CacheVarLiteral.LOC_SHAPE -> Int::class
                CacheVarLiteral.COMPONENT -> ComponentType::class
                CacheVarLiteral.STRUCT -> Int::class
                CacheVarLiteral.IDKIT -> Int::class
                CacheVarLiteral.OVERLAY_INTERFACE -> Int::class
                CacheVarLiteral.MIDI -> Int::class
                CacheVarLiteral.NPCMODE -> Int::class
                CacheVarLiteral.NAMEDOBJ -> UnpackedObjType::class
                CacheVarLiteral.SYNTH -> SynthType::class
                CacheVarLiteral.AREA -> Int::class
                CacheVarLiteral.STAT -> StatType::class
                CacheVarLiteral.NPCSTAT -> Int::class
                CacheVarLiteral.MAPAREA -> Int::class
                CacheVarLiteral.INTERFACE -> Int::class
                CacheVarLiteral.COORDGRID -> CoordGrid::class
                CacheVarLiteral.GRAPHIC -> Int::class
                CacheVarLiteral.FONTMETRICS -> Int::class
                CacheVarLiteral.ENUM -> EnumType::class
                CacheVarLiteral.JINGLE -> Int::class
                CacheVarLiteral.INT -> Int::class
                CacheVarLiteral.LOC -> LocType::class
                CacheVarLiteral.MODEL -> Int::class
                CacheVarLiteral.NPC -> NpcType::class
                CacheVarLiteral.OBJ -> ObjType::class
                CacheVarLiteral.PLAYERUID -> Int::class
                CacheVarLiteral.STRING -> String::class
                CacheVarLiteral.SPOTANIM -> SpotanimType::class
                CacheVarLiteral.NPCUID -> Int::class
                CacheVarLiteral.INV -> Int::class
                CacheVarLiteral.TEXTURE -> Int::class
                CacheVarLiteral.CATEGORY -> Int::class
                CacheVarLiteral.CHAR -> Int::class
                CacheVarLiteral.MAPELEMENT -> Int::class
                CacheVarLiteral.HITMARK -> Int::class
                CacheVarLiteral.STRINGVECTOR -> Int::class
                CacheVarLiteral.DBTABLE -> Int::class
                CacheVarLiteral.DBROW -> Int::class
                CacheVarLiteral.MOVESPEED -> Int::class
                CacheVarLiteral.VARBIT -> VarBitType::class
                CacheVarLiteral.VARP -> VarpType::class
            }

    public fun <K, V : Any> findCodec(literal: CacheVarLiteral): CacheVarCodec<K, V> =
        findCodec(literal.codecOut)

    @Suppress("UNCHECKED_CAST")
    public fun <K, V : Any> findCodec(type: KClass<*>): CacheVarCodec<K, V> =
        codecMap[type] as? CacheVarCodec<K, V>
            ?: error("CacheVarCodec for type is not implemented in `codecMap`: $type")
}
