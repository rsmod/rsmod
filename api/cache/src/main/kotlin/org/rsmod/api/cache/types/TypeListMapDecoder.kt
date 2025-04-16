package org.rsmod.api.cache.types

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.openrs2.cache.Cache
import org.rsmod.api.cache.types.category.CategoryTypeDecoder
import org.rsmod.api.cache.types.comp.ComponentTypeDecoder
import org.rsmod.api.cache.types.enums.EnumTypeDecoder
import org.rsmod.api.cache.types.font.FontMetricsDecoder
import org.rsmod.api.cache.types.gameval.GameValDecoder
import org.rsmod.api.cache.types.headbar.HeadbarTypeDecoder
import org.rsmod.api.cache.types.hitmark.HitmarkTypeDecoder
import org.rsmod.api.cache.types.inv.InvTypeDecoder
import org.rsmod.api.cache.types.jingle.JingleTypeDecoder
import org.rsmod.api.cache.types.loc.LocTypeDecoder
import org.rsmod.api.cache.types.midi.MidiTypeDecoder
import org.rsmod.api.cache.types.npc.NpcTypeDecoder
import org.rsmod.api.cache.types.obj.ObjTypeDecoder
import org.rsmod.api.cache.types.param.ParamTypeDecoder
import org.rsmod.api.cache.types.proj.ProjAnimTypeDecoder
import org.rsmod.api.cache.types.seq.SeqTypeDecoder
import org.rsmod.api.cache.types.spot.SpotanimTypeDecoder
import org.rsmod.api.cache.types.stat.StatTypeDecoder
import org.rsmod.api.cache.types.struct.StructTypeDecoder
import org.rsmod.api.cache.types.synth.SynthTypeDecoder
import org.rsmod.api.cache.types.varbit.VarBitTypeDecoder
import org.rsmod.api.cache.types.varn.VarnTypeDecoder
import org.rsmod.api.cache.types.varnbit.VarnBitTypeDecoder
import org.rsmod.api.cache.types.varp.VarpTypeDecoder
import org.rsmod.api.cache.types.walktrig.WalkTriggerTypeDecoder
import org.rsmod.api.cache.util.ComplexTypeDecoder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.CacheType
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.TypeResolver
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
        val varnbits = decode { VarnBitTypeDecoder.decodeAll(cache) }
        val hitmarks = decode { HitmarkTypeDecoder.decodeAll(cache) }
        val headbars = decode { HeadbarTypeDecoder.decodeAll(cache) }
        val categories = CategoryTypeDecoder.decodeAll(names)
        val projanims = decode { ProjAnimTypeDecoder.decodeAll(cache) }
        val midis = MidiTypeDecoder.decodeAll(names)
        val gameVals = GameValDecoder.decodeAll(cache)
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
                varnbits = varnbits.await(),
                hitmarks = hitmarks.await(),
                headbars = headbars.await(),
                categories = categories,
                projanims = projanims.await(),
                midis = midis,
                gameVals = gameVals,
            )
            .apply {
                assignInternal(this.objs, names.objs)
                assignInternal(this.locs, names.locs)
                assignInternal(this.npcs, names.npcs)
                assignInternal(this.params, names.params)
                assignInternal(this.enums, names.enums)
                assignInternal(this.components, names.components)
                assignInternal(this.interfaces, names.interfaces)
                assignInternal(this.invs, names.invs)
                assignInternal(this.varps, names.varps)
                assignInternal(this.varbits, names.varbits)
                VarBitTypeDecoder.assignBaseVars(this.varbits, this.varps)
                assignInternal(this.seqs, names.seqs)
                assignInternal(this.spotanims, names.spotanims)
                assignInternal(this.stats, names.stats)
                assignInternal(this.structs, names.structs)
                assignInternal(this.fonts, names.fonts)
                assignInternal(this.walkTriggers, names.walkTriggers)
                assignInternal(this.varns, names.varns)
                assignInternal(this.varnbits, names.varnbits)
                VarnBitTypeDecoder.assignBaseVars(this.varnbits, this.varns)
                assignInternal(this.hitmarks, names.hitmarks)
                assignInternal(this.headbars, names.headbars)
                assignInternal(this.projanims, names.projanims)
                assignInternal(this.midis, names.midis)
                ComplexTypeDecoder.decodeAll(this)
            }
    }

    private fun <T> CoroutineScope.decode(decode: suspend () -> T): Deferred<T> = async { decode() }

    private fun assignInternal(list: Map<Int, CacheType>, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }
}
