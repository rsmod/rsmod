package org.rsmod.api.cache.types

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.openrs2.cache.Cache
import org.rsmod.api.cache.types.comp.ComponentTypeDecoder
import org.rsmod.api.cache.types.enums.EnumTypeDecoder
import org.rsmod.api.cache.types.font.FontMetricsDecoder
import org.rsmod.api.cache.types.inv.InvTypeDecoder
import org.rsmod.api.cache.types.jingle.JingleTypeDecoder
import org.rsmod.api.cache.types.loc.LocTypeDecoder
import org.rsmod.api.cache.types.npc.NpcTypeDecoder
import org.rsmod.api.cache.types.obj.ObjTypeDecoder
import org.rsmod.api.cache.types.param.ParamTypeDecoder
import org.rsmod.api.cache.types.seq.SeqTypeDecoder
import org.rsmod.api.cache.types.spot.SpotanimTypeDecoder
import org.rsmod.api.cache.types.stat.StatTypeDecoder
import org.rsmod.api.cache.types.struct.StructTypeDecoder
import org.rsmod.api.cache.types.synth.SynthTypeDecoder
import org.rsmod.api.cache.types.varbit.VarBitTypeDecoder
import org.rsmod.api.cache.types.varn.VarnTypeDecoder
import org.rsmod.api.cache.types.varp.VarpTypeDecoder
import org.rsmod.api.cache.types.walktrig.WalkTriggerTypeDecoder
import org.rsmod.api.cache.util.ComplexTypeDecoder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.interf.InterfaceTypeList

public object TypeListMapDecoder {
    public fun ofParallel(cache: Cache, names: NameMapping): TypeListMap = runBlocking {
        val locs = decode { LocTypeDecoder.decodeAll(cache) }
        val objs = decode { ObjTypeDecoder.decodeAll(cache) }
        val npcs = decode { NpcTypeDecoder.decodeAll(cache) }
        val params = decode { ParamTypeDecoder.decodeAll(cache) }
        val enums = decode { EnumTypeDecoder.decodeAll(cache) }
        val varps = decode { VarpTypeDecoder.decodeAll(cache) }
        val varbits = decode { VarBitTypeDecoder.decodeAll(cache) }
        val components = decode { ComponentTypeDecoder.decodeAll(cache) }
        val interfaces = decode {
            val componentList = components.await()
            InterfaceTypeList.from(componentList.values)
        }
        val invs = decode { InvTypeDecoder.decodeAll(cache) }
        val seqs = decode { SeqTypeDecoder.decodeAll(cache) }
        val fonts = decode { FontMetricsDecoder.decodeAll(cache) }
        val stats = decode { StatTypeDecoder.decodeAll(cache, names.stats) }
        val synths = SynthTypeDecoder.decodeAll(names)
        val structs = decode { StructTypeDecoder.decodeAll(cache) }
        val spotanims = decode { SpotanimTypeDecoder.decodeAll(cache) }
        val jingles = JingleTypeDecoder.decodeAll(names)
        val walkTriggers = decode { WalkTriggerTypeDecoder.decodeAll(cache, names.walkTriggers) }
        val varns = decode { VarnTypeDecoder.decodeAll(cache, names.varns) }
        TypeListMap(
                locs = locs.await(),
                objs = objs.await(),
                npcs = npcs.await(),
                params = params.await(),
                enums = enums.await(),
                components = components.await(),
                interfaces = interfaces.await(),
                varps = varps.await(),
                varbits = varbits.await(),
                invs = invs.await(),
                seqs = seqs.await(),
                fonts = fonts.await(),
                stats = stats.await(),
                spotanims = spotanims.await(),
                synths = synths,
                structs = structs.await(),
                jingles = jingles,
                walkTriggers = walkTriggers.await(),
                varns = varns.await(),
            )
            .apply {
                ObjTypeDecoder.assignInternal(this.objs, names.objs)
                LocTypeDecoder.assignInternal(this.locs, names.locs)
                NpcTypeDecoder.assignInternal(this.npcs, names.npcs)
                ParamTypeDecoder.assignInternal(this.params, names.params)
                EnumTypeDecoder.assignInternal(this.enums, names.enums)
                ComponentTypeDecoder.assignInternal(this.components, names.components)
                InterfaceTypeList.assignInternal(this.interfaces, names.interfaces)
                InvTypeDecoder.assignInternal(this.invs, names.invs)
                VarpTypeDecoder.assignInternal(this.varps, names.varps)
                VarBitTypeDecoder.assignInternal(this.varbits, names.varbits)
                VarBitTypeDecoder.assignBaseVars(this.varbits, this.varps)
                SeqTypeDecoder.assignInternal(this.seqs, names.seqs)
                SpotanimTypeDecoder.assignInternal(this.spotanims, names.spotanims)
                StatTypeDecoder.assignInternal(this.stats, names.stats)
                StructTypeDecoder.assignInternal(this.structs, names.structs)
                FontMetricsDecoder.assignInternal(this.fonts, names.fonts)
                WalkTriggerTypeDecoder.assignInternal(this.walkTriggers, names.walkTriggers)
                VarnTypeDecoder.assignInternal(this.varns, names.varns)
                ComplexTypeDecoder.decodeAll(this)
            }
    }

    private fun <T> CoroutineScope.decode(decode: suspend () -> T): Deferred<T> = async { decode() }
}
