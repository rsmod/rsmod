package org.rsmod.api.cache.enricher.obj

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.rsmod.api.config.aliases.ParamInt
import org.rsmod.api.config.aliases.ParamSeq
import org.rsmod.api.config.aliases.ParamStat
import org.rsmod.api.config.aliases.ParamStr
import org.rsmod.api.config.aliases.ParamSynth
import org.rsmod.api.config.refs.params
import org.rsmod.api.parsers.toml.Toml
import org.rsmod.api.type.script.dsl.ObjPluginBuilder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.api.utils.io.InputStreams
import org.rsmod.game.type.obj.Dummyitem
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.WeaponCategory
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.game.type.synth.SynthType

public class DefaultObjCacheEnricher
@Inject
constructor(
    @Toml private val mapper: ObjectMapper,
    private val nameMapping: NameMapping,
    private val seqTypes: SeqTypeList,
    private val statTypes: StatTypeList,
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
        this.tradeable = config.untradable == null || !config.untradable
        this.dummyitem = config.dummyitem?.let(Dummyitem::get)
        putSeq(config.walkAnim, params.bas_walk_f)
        putSeq(config.runAnim, params.bas_running)
        putSeq(config.readyAnim, params.bas_readyanim)
        putSeq(config.turnAnim, params.bas_turnonspot)
        putSeq(config.walkAnimBack, params.bas_walk_b)
        putSeq(config.walkAnimLeft, params.bas_walk_l)
        putSeq(config.walkAnimRight, params.bas_walk_r)
        putSeq(config.animStance1, params.attack_anim_stance1)
        putSeq(config.animStance2, params.attack_anim_stance2)
        putSeq(config.animStance3, params.attack_anim_stance3)
        putSeq(config.animStance4, params.attack_anim_stance4)
        putSynth(config.soundStance1, params.attack_sound_stance1)
        putSynth(config.soundStance2, params.attack_sound_stance2)
        putSynth(config.soundStance3, params.attack_sound_stance3)
        putSynth(config.soundStance4, params.attack_sound_stance4)
        putSeq(config.blockAnim, params.defend_anim)
        putSynth(config.equipmentSound, params.equipment_sound)
        putStr(config.destroyHeader, params.destroy_note_title)
        putStr(config.destroyNote, params.destroy_note_desc)
        putInt(config.respawnTimer, params.respawn_time)
        putInt(config.speed, params.attackrate)
        putInt(config.range, params.attackrange)
        putStatReq(config.reqStat1, params.statreq1_skill, config.reqLevel1, params.statreq1_level)
        putStatReq(config.reqStat2, params.statreq2_skill, config.reqLevel2, params.statreq2_level)
        putWeaponCategory(config.weaponCategory)
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

    private fun ObjPluginBuilder.putStatReq(
        stat: String?,
        statParam: ParamStat,
        level: Int?,
        levelParam: ParamInt,
    ) {
        if (stat == null) {
            return
        }
        val statType = statTypes.values.firstOrNull { it.internalName == stat }?.toHashedType()
        param[statParam] = statType ?: error("Invalid stat name: $stat")
        param[levelParam] = level ?: error("Invalid null level: '$name' ($levelParam, $statParam)")
    }

    private fun ObjPluginBuilder.putWeaponCategory(categoryIdentifier: String?) {
        // Note: This assumes `WeaponCategory` entry names match `weapon_category` strings in
        // `ExternalObjConfig`.
        val weaponCategory = categoryIdentifier?.let(WeaponCategory::valueOf)
        if (weaponCategory != null && weaponCategory != WeaponCategory.Unarmed) {
            this.weaponCategory = weaponCategory.id
        }
    }

    private companion object {
        const val CONFIG_MAP_KEY: String = "config"
    }
}

private data class ExternalObjConfig(
    val obj: String,
    val reqStat1: String?,
    val reqLevel1: Int?,
    val reqStat2: String?,
    val reqLevel2: Int?,
    val walkAnim: Int?,
    val runAnim: Int?,
    val readyAnim: Int?,
    val turnAnim: Int?,
    val walkAnimBack: Int?,
    val walkAnimLeft: Int?,
    val walkAnimRight: Int?,
    val animStance1: Int?,
    val soundStance1: Int?,
    val animStance2: Int?,
    val soundStance2: Int?,
    val animStance3: Int?,
    val soundStance3: Int?,
    val animStance4: Int?,
    val soundStance4: Int?,
    val blockAnim: Int?,
    val equipmentSound: Int?,
    val destroyHeader: String?,
    val destroyNote: String?,
    val destroyNoteAlt: String?,
    val respawnTimer: Int?,
    val untradable: Boolean?,
    val dummyitem: Int?,
    val speed: Int?,
    val range: Int?,
    val weaponCategory: String?,
)
