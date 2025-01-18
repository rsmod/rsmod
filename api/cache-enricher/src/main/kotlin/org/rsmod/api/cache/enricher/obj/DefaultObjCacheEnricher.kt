package org.rsmod.api.cache.enricher.obj

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.rsmod.api.config.aliases.ParamInt
import org.rsmod.api.config.aliases.ParamSeq
import org.rsmod.api.config.aliases.ParamStr
import org.rsmod.api.config.aliases.ParamSynth
import org.rsmod.api.config.refs.params
import org.rsmod.api.parsers.toml.Toml
import org.rsmod.api.type.script.dsl.ObjPluginBuilder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.api.utils.io.InputStreams
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.synth.SynthType

public class DefaultObjCacheEnricher
@Inject
constructor(
    @Toml private val mapper: ObjectMapper,
    private val nameMapping: NameMapping,
    private val seqTypes: SeqTypeList,
) : ObjCacheEnricher {
    private val names: Map<String, Int>
        get() = nameMapping.objs

    override fun generate(): List<UnpackedObjType> {
        val external = loadExternalConfigs()
        return external.map { it.toCacheType() }
    }

    private fun loadExternalConfigs(): List<ExternalObjConfig> {
        val input = InputStreams.readAllBytes<DefaultObjCacheEnricher>("objs.toml")
        val type = object : TypeReference<Map<String, List<ExternalObjConfig>>>() {}
        val map = mapper.readValue(input, type)
        val configs =
            map[CONFIG_MAP_KEY] ?: error("Could not extract `${CONFIG_MAP_KEY}` value from input.")
        return configs
    }

    private fun ExternalObjConfig.toCacheType(): UnpackedObjType {
        val id = names[obj] ?: error("Mapping with name not found: $obj")
        val builder = ObjPluginBuilder(obj)
        return builder.apply(this).build(id)
    }

    private fun ObjPluginBuilder.apply(config: ExternalObjConfig): ObjPluginBuilder {
        putSeq(config.walkAnim, params.bas_walk_f)
        putSeq(config.runAnim, params.bas_running)
        putSeq(config.readyAnim, params.bas_readyanim)
        putSeq(config.turnAnim, params.bas_turnonspot)
        putSeq(config.walkAnimBack, params.bas_walk_b)
        putSeq(config.walkAnimLeft, params.bas_walk_l)
        putSeq(config.walkAnimRight, params.bas_walk_r)
        putSeq(config.accurateAnim, params.attack_anim_accurate)
        putSeq(config.aggressiveAnim, params.attack_anim_aggressive)
        putSeq(config.controlledAnim, params.attack_anim_controlled)
        putSeq(config.defensiveAnim, params.attack_anim_defensive)
        putSynth(config.accurateSound, params.attack_sound_accurate)
        putSynth(config.aggressiveSound, params.attack_sound_aggressive)
        putSynth(config.controlledSound, params.attack_sound_controlled)
        putSynth(config.defensiveSound, params.attack_sound_defensive)
        putSeq(config.blockAnim, params.defend_anim)
        putSynth(config.equipmentSound, params.equipment_sound)
        putStr(config.destroyHeader, params.destroy_note_title)
        putStr(config.destroyNote, params.destroy_note_desc)
        putInt(config.respawnTimer, params.respawn_time)
        putInt(config.speed, params.attackrate)
        putInt(config.range, params.attackrange)
        return this
    }

    private fun ObjPluginBuilder.putSeq(anim: Int?, paramType: ParamSeq) {
        val id = anim ?: return
        val seq = seqTypes.getValue(id)
        param[paramType] = seq.toHashedType()
    }

    private fun ObjPluginBuilder.putSynth(sound: Int?, paramType: ParamSynth) {
        val id = sound ?: return
        val synth = SynthType(id, "synth_$id")
        param[paramType] = synth
    }

    private fun ObjPluginBuilder.putInt(value: Int?, paramType: ParamInt) {
        if (value != null) {
            param[paramType] = value
        }
    }

    private fun ObjPluginBuilder.putStr(value: String?, paramType: ParamStr) {
        if (value != null) {
            param[paramType] = value
        }
    }

    private companion object {
        const val CONFIG_MAP_KEY: String = "config"
    }
}

private data class ExternalObjConfig(
    val obj: String,
    val walkAnim: Int?,
    val runAnim: Int?,
    val readyAnim: Int?,
    val turnAnim: Int?,
    val walkAnimBack: Int?,
    val walkAnimLeft: Int?,
    val walkAnimRight: Int?,
    val accurateAnim: Int?,
    val accurateSound: Int?,
    val aggressiveAnim: Int?,
    val aggressiveSound: Int?,
    val controlledAnim: Int?,
    val controlledSound: Int?,
    val defensiveAnim: Int?,
    val defensiveSound: Int?,
    val blockAnim: Int?,
    val equipmentSound: Int?,
    val destroyHeader: String?,
    val destroyNote: String?,
    val respawnTimer: Int?,
    val speed: Int?,
    val range: Int?,
    val combatStyle: String?,
)
