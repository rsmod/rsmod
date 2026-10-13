package org.runerealm.api.cache.types

import org.openrs2.cache.Cache
import org.runerealm.api.cache.types.area.AreaTypeDecoder
import org.runerealm.api.cache.types.category.CategoryTypeDecoder
import org.runerealm.api.cache.types.clientscript.ClientScriptTypeDecoder
import org.runerealm.api.cache.types.comp.ComponentTypeDecoder
import org.runerealm.api.cache.types.dbrow.DbRowTypeDecoder
import org.runerealm.api.cache.types.dbtable.DbTableTypeDecoder
import org.runerealm.api.cache.types.enums.EnumTypeDecoder
import org.runerealm.api.cache.types.font.FontMetricsDecoder
import org.runerealm.api.cache.types.gameval.GameValDecoder
import org.runerealm.api.cache.types.headbar.HeadbarTypeDecoder
import org.runerealm.api.cache.types.hitmark.HitmarkTypeDecoder
import org.runerealm.api.cache.types.hunt.HuntModeTypeDecoder
import org.runerealm.api.cache.types.idk.IdentKitTypeDecoder
import org.runerealm.api.cache.types.inv.InvTypeDecoder
import org.runerealm.api.cache.types.jingle.JingleTypeDecoder
import org.runerealm.api.cache.types.loc.LocTypeDecoder
import org.runerealm.api.cache.types.midi.MidiTypeDecoder
import org.runerealm.api.cache.types.mod.ModLevelTypeDecoder
import org.runerealm.api.cache.types.model.ModelTypeSimpleDecoder
import org.runerealm.api.cache.types.npc.NpcTypeDecoder
import org.runerealm.api.cache.types.obj.ObjTypeDecoder
import org.runerealm.api.cache.types.param.ParamTypeDecoder
import org.runerealm.api.cache.types.proj.ProjAnimTypeDecoder
import org.runerealm.api.cache.types.seq.SeqTypeDecoder
import org.runerealm.api.cache.types.spot.SpotanimTypeDecoder
import org.runerealm.api.cache.types.stat.StatTypeDecoder
import org.runerealm.api.cache.types.struct.StructTypeDecoder
import org.runerealm.api.cache.types.synth.SynthTypeDecoder
import org.runerealm.api.cache.types.varbit.VarBitTypeDecoder
import org.runerealm.api.cache.types.varn.VarnTypeDecoder
import org.runerealm.api.cache.types.varnbit.VarnBitTypeDecoder
import org.runerealm.api.cache.types.varp.VarpTypeDecoder
import org.runerealm.api.cache.types.walktrig.WalkTriggerTypeDecoder
import org.runerealm.api.cache.util.ComplexTypeDecoder
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.CacheType
import org.runerealm.game.type.TypeListMap
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.interf.InterfaceTypeList

public object TypeListMapDecoder {
    public fun from(cache: Cache, names: NameMapping): TypeListMap {
        val locs = LocTypeDecoder.decodeAll(cache)
        val objs = ObjTypeDecoder.decodeAll(cache)
        val npcs = NpcTypeDecoder.decodeAll(cache)
        val params = ParamTypeDecoder.decodeAll(cache)
        val enums = EnumTypeDecoder.decodeAll(cache)
        val varps = VarpTypeDecoder.decodeAll(cache)
        val varbits = VarBitTypeDecoder.decodeAll(cache)
        val components = ComponentTypeDecoder.decodeAll(cache)
        val interfaces = InterfaceTypeList.from(components.values)
        val invs = InvTypeDecoder.decodeAll(cache)
        val seqs = SeqTypeDecoder.decodeAll(cache)
        val fonts = FontMetricsDecoder.decodeAll(cache)
        val stats = StatTypeDecoder.decodeAll(cache, names.stats)
        val synths = SynthTypeDecoder.decodeAll(names)
        val structs = StructTypeDecoder.decodeAll(cache)
        val spotanims = SpotanimTypeDecoder.decodeAll(cache)
        val jingles = JingleTypeDecoder.decodeAll(names)
        val walkTriggers = WalkTriggerTypeDecoder.decodeAll(cache, names.walkTriggers)
        val varns = VarnTypeDecoder.decodeAll(cache, names.varns)
        val varnbits = VarnBitTypeDecoder.decodeAll(cache)
        val hitmarks = HitmarkTypeDecoder.decodeAll(cache)
        val headbars = HeadbarTypeDecoder.decodeAll(cache)
        val categories = CategoryTypeDecoder.decodeAll(names)
        val projanims = ProjAnimTypeDecoder.decodeAll(cache)
        val midis = MidiTypeDecoder.decodeAll(names)
        val gameVals = GameValDecoder.decodeAll(cache)
        val areas = AreaTypeDecoder.decodeAll(cache)
        val dbTables = DbTableTypeDecoder.decodeAll(cache)
        val dbRows = DbRowTypeDecoder.decodeAll(cache)
        val hunt = HuntModeTypeDecoder.decodeAll(cache)
        val modLevels = ModLevelTypeDecoder.decodeAll(cache)
        val clientscripts = ClientScriptTypeDecoder.decodeAll(cache)
        val models = ModelTypeSimpleDecoder.decodeAll(cache)
        val identKits = IdentKitTypeDecoder.decodeAll(cache)

        val typeList =
            TypeListMap(
                locs = locs,
                objs = objs,
                npcs = npcs,
                params = params,
                enums = enums,
                components = components,
                interfaces = interfaces,
                varps = varps,
                varbits = varbits,
                invs = invs,
                seqs = seqs,
                fonts = fonts,
                stats = stats,
                spotanims = spotanims,
                synths = synths,
                structs = structs,
                jingles = jingles,
                walkTriggers = walkTriggers,
                varns = varns,
                varnbits = varnbits,
                hitmarks = hitmarks,
                headbars = headbars,
                categories = categories,
                projanims = projanims,
                midis = midis,
                gameVals = gameVals,
                areas = areas,
                dbTables = dbTables,
                dbRows = dbRows,
                hunt = hunt,
                modLevels = modLevels,
                clientscripts = clientscripts,
                models = models,
                identKits = identKits,
            )

        return typeList.apply {
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
            assignInternal(this.areas, names.areas)
            assignInternal(this.dbTables, names.dbTables)
            assignInternal(this.dbRows, names.dbRows)
            assignInternal(this.hunt, names.hunt)
            assignInternal(this.modLevels, names.modLevels)
            assignInternal(this.clientscripts, names.clientscripts)
            assignInternal(this.models, names.models)
            ComplexTypeDecoder.decodeAll(this)
        }
    }

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
