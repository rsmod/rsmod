package org.rsmod.content.interfaces.emotes

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.jingles
import org.rsmod.api.config.refs.npcs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.back
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.script.onPlayerWalkTrigger
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.map.Direction
import org.rsmod.game.map.translate
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.util.EnumTypeMap
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EmotesScript
@Inject
private constructor(
    private val eventBus: EventBus,
    private val enumResolver: EnumTypeMapResolver,
    private val protectedAccess: ProtectedAccessLauncher,
    private val npcTypes: NpcTypeList,
    private val npcRepo: NpcRepository,
    private val seqTypes: SeqTypeList,
    private val skillCapeEmotes: SkillCapeEmoteResolver,
) : PluginScript() {
    private lateinit var emoteSlots: EnumTypeMap<Int, String>
    private lateinit var emoteSlotRange: IntRange

    override fun ScriptContext.startup() {
        loadEmotesEnum()
        loadSkillCapeEmotes()
        onIfOpen(interfaces.emote) { player.onTabOpen() }
        onIfOverlayButton(emote_components.emote_list) { player.selectEmote(comsub, op) }
        onPlayerWalkTrigger(emote_walktriggers.cancelanim) { player.resetAnim() }
    }

    private fun loadEmotesEnum() {
        emoteSlots = enumResolver[emote_enums.emote_names]

        val comsubSlots = emoteSlots.keys
        emoteSlotRange = comsubSlots.min()..comsubSlots.max()
    }

    private fun loadSkillCapeEmotes() {
        skillCapeEmotes.startup()
    }

    private fun Player.selectEmote(emoteSlot: Int, op: IfButtonOp) {
        val emote = emoteSlots.getValue(emoteSlot)
        ifClose(eventBus)
        protectedAccess.launch(this) { selectEmote(emote, op) }
    }

    private suspend fun ProtectedAccess.selectEmote(emote: String, op: IfButtonOp) {
        when (emote) {
            "Yes" -> loopAnim(seqs.emote_yes, seqs.emote_yes_loop, op)
            "No" -> loopAnim(seqs.emote_no, seqs.emote_no_loop, op)
            "Bow" -> loopAnim(seqs.emote_bow, seqs.emote_bow_loop, op)
            "Angry" -> loopAnim(seqs.emote_angry, seqs.emote_angry_loop, op)
            "Think" -> loopAnim(seqs.emote_think, seqs.emote_think_loop, op)
            "Wave" -> loopAnim(seqs.emote_wave, seqs.emote_wave_loop, op)
            "Shrug" -> loopAnim(seqs.emote_shrug, seqs.emote_shrug_loop, op)
            "Cheer" -> loopAnim(seqs.emote_cheer, seqs.emote_cheer_loop, op)
            "Beckon" -> loopAnim(seqs.emote_beckon, seqs.emote_beckon_loop, op)
            "Laugh" -> loopAnim(seqs.emote_laugh, seqs.emote_laugh_loop, op)
            "Jump for Joy" -> loopAnim(seqs.emote_jump_with_joy, seqs.emote_jump_with_joy_loop, op)
            "Yawn" -> loopAnim(seqs.emote_yawn, seqs.emote_yawn_loop, op)
            "Dance" -> loopAnim(seqs.emote_dance, seqs.emote_dance_loop, op)
            "Jig" -> loopAnim(seqs.emote_dance_scottish, seqs.emote_dance_scottish_loop, op)
            "Spin" -> loopAnim(seqs.emote_dance_spin, seqs.emote_dance_spin_loop, op)
            "Headbang" -> loopAnim(seqs.emote_dance_headbang, seqs.emote_dance_headbang_loop, op)
            "Cry" -> loopAnim(seqs.emote_cry, seqs.emote_cry_loop, op)
            "Blow Kiss" -> loopAnim(seqs.emote_blow_kiss, seqs.emote_blow_kiss_loop, op)
            "Panic" -> loopAnim(seqs.emote_panic, seqs.emote_panic_loop, op)
            "Raspberry" -> loopAnim(seqs.emote_ya_boo_sucks, seqs.emote_ya_boo_sucks_loop, op)
            "Clap" -> loopAnim(seqs.emote_clap, seqs.emote_clap_loop, op)
            "Salute" -> loopAnim(seqs.emote_fremmenik_salute, seqs.emote_fremmenik_salute_loop, op)
            "Goblin Bow" ->
                lockedLoopAnimDialog(
                    seqs.human_cave_goblin_bow,
                    seqs.human_cave_goblin_bow_loop,
                    varbits.lost_tribe_progress,
                    "This emote can be unlocked during the Lost Tribe quest.",
                    op = op,
                    varbitStateReq = 7,
                )
            "Goblin Salute" ->
                lockedLoopAnimDialog(
                    seqs.human_cave_goblin_dance,
                    seqs.human_cave_goblin_dance_loop,
                    varbits.lost_tribe_progress,
                    "This emote can be unlocked during the Lost Tribe quest.",
                    op = op,
                    varbitStateReq = 7,
                )
            "Glass Box" ->
                lockedLoopAnimDialog(
                    seqs.emote_glass_box,
                    seqs.emote_glass_box_loop,
                    varbits.emote_glassbox,
                    "This emote can be unlocked during the mime random event.",
                    op = op,
                )
            "Climb Rope" ->
                lockedLoopAnimDialog(
                    seqs.emote_climbing_rope,
                    seqs.emote_climbing_rope_loop,
                    varbits.emote_climbrope,
                    "This emote can be unlocked during the mime random event.",
                    op = op,
                )
            "Lean" ->
                lockedLoopAnimDialog(
                    seqs.emote_mime_lean,
                    seqs.emote_mime_lean_loop,
                    varbits.emote_lean,
                    "This emote can be unlocked during the mime random event.",
                    op = op,
                )
            "Glass Wall" ->
                lockedLoopAnimDialog(
                    seqs.emote_glass_wall,
                    seqs.emote_glass_wall_loop,
                    varbits.emote_glasswall,
                    "This emote can be unlocked during the mime random event.",
                    op = op,
                )
            "Idea" ->
                lockedLoopAnimDialog(
                    seqs.emote_lightbulb,
                    seqs.emote_lightbulb_loop,
                    varbits.sos_emote_idea,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                    spot = spotanims.emote_lightbulb_spot,
                    op = op,
                )
            "Stamp" ->
                lockedLoopAnimDialog(
                    seqs.emote_stampfeet,
                    seqs.emote_stampfeet_loop,
                    varbits.sos_emote_stamp,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                    spot = spotanims.emote_duststamp_spot,
                    op = op,
                )
            "Flap" ->
                lockedAnimDialog(
                    flapEmoteSelector(op),
                    varbits.sos_emote_flap,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                )
            "Slap Head" ->
                lockedLoopAnimDialog(
                    seqs.emote_slap_head,
                    seqs.emote_slap_head_loop,
                    varbits.sos_emote_idea,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                    op = op,
                )
            "Zombie Walk" ->
                lockedLoopAnimDialog(
                    seqs.zombie_walk_emote,
                    seqs.zombie_walk_emote_loop,
                    varbits.emote_zombie_walk,
                    "This emote can be unlocked during the gravedigger random event.",
                    op = op,
                )
            "Zombie Dance" ->
                lockedLoopAnimDialog(
                    seqs.zombie_dance,
                    seqs.zombie_dance_loop,
                    varbits.emote_zombie_dance,
                    "This emote can be unlocked during the gravedigger random event.",
                    op = op,
                )
            "Scared" ->
                lockedLoopAnimDialog(
                    seqs.terrified_emote,
                    seqs.terrified_emote_loop,
                    varbits.emote_terrified,
                    "This emote can be unlocked by doing a Halloween seasonal event.",
                    op = op,
                )
            "Rabbit Hop" ->
                lockedLoopAnimDialog(
                    seqs.rabbit_emote,
                    seqs.rabbit_emote_loop,
                    varbits.emote_bunny_hop,
                    "This emote can be unlocked by doing an Easter seasonal event.",
                    op = op,
                )
            "Sit up" ->
                lockedLoopAnimDialog(
                    seqs.emote_situps_5,
                    seqs.emote_situps_5_loop,
                    varbits.emote_drilldemon,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                    op = op,
                )
            "Push up" ->
                lockedLoopAnimDialog(
                    seqs.emote_pushups_5,
                    seqs.emote_pushups_5_loop,
                    varbits.emote_drilldemon,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                    op = op,
                )
            "Star jump" ->
                lockedLoopAnimDialog(
                    seqs.emote_starjump_5,
                    seqs.emote_starjump_5_loop,
                    varbits.emote_drilldemon,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                    op = op,
                )
            "Jog" ->
                lockedLoopAnimDialog(
                    seqs.emote_run_on_spot,
                    seqs.emote_run_on_spot_loop,
                    varbits.emote_drilldemon,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                    op = op,
                )
            "Flex" ->
                lockedLoopAnimDialog(
                    seqs.emote_flex,
                    seqs.emote_flex_loop,
                    varbits.emote_flex,
                    "You can unlock this emote by completing Checkal's task in Below Ice Mountain.",
                    op = op,
                )
            "Zombie Hand" ->
                lockedAnimDialog(
                    seqs.hw07_arm_from_the_ground_emote,
                    varbits.emote_zombie_hand,
                    "This emote can be unlocked by doing a Halloween seasonal event.",
                )
            "Hypermobile Drinker" ->
                lockedLoopAnimDialog(
                    seqs.ash_emote,
                    seqs.ash_emote_loop,
                    varbits.emote_ash,
                    "This emote can be unlocked by doing a Halloween seasonal event.",
                    op = op,
                )
            "Skill Cape" -> skillCapeEmote()
            "Air Guitar" -> airGuitarEmote()
            "Uri transform" -> uriTransformEmote()
            "Smooth dance" ->
                lockedAnimDialog(
                    seqs.bday17_bling,
                    varbits.emote_hotline_bling,
                    "This emote can be unlocked by doing a birthday event.",
                )
            "Crazy dance" ->
                lockedAnimDialog(
                    crazyDanceEmoteSelector(op),
                    varbits.emote_gangnam,
                    "This emote can be unlocked by doing a birthday event.",
                )
            "Premier Shield" -> premierShieldEmote()
            "Explore" ->
                lockedLoopAnimDialog(
                    seqs.emote_explore,
                    seqs.emote_explore_loop,
                    varbits.emote_explore,
                    "This emote can be unlocked by completing at least 600 beginner clue scrolls.",
                    op = op,
                )
            "Relic unlock" -> relicUnlockEmote()
            "Party" ->
                lockedLoopAnimDialog(
                    seqs.emote_party,
                    seqs.emote_party_loop,
                    varbits.emote_party,
                    "This emote can be unlocked by doing a birthday event.",
                    spot = spotanims.fx_emote_party01_active,
                    op = op,
                )
            "Trick" ->
                lockedAnimDialog(
                    seqs.emote_trick,
                    varbits.emote_trick,
                    "This emote can be unlocked by doing a Halloween event.",
                    spot = spotanims.hw23_emote_bat_spotanim,
                )
            "Fortis Salute" -> fortisSaluteEmote(loop = op == IfButtonOp.Op2)
            "Crab dance" -> {
                /* Emote is not available in the tab. */
            }
            "Sit down" -> loopAnim(seqs.emote_sit_loop, seqs.emote_sit, op)
            else -> throw NotImplementedError("Emote not implemented: $emote")
        }
    }

    private fun ProtectedAccess.playAnim(seq: SeqType, spot: SpotanimType?) {
        anim(seq)
        spot?.let(::spotanim)
        publishEmoteEvent(seq)
    }

    private fun ProtectedAccess.simpleAnim(seq: SeqType, spot: SpotanimType? = null) {
        if (seq.requiresWalkTrigger() && !trySetWalkTrigger(emote_walktriggers.cancelanim)) {
            return
        }
        stopAction()
        playAnim(seq, spot)
    }

    private fun ProtectedAccess.loopAnim(
        seqOp1: SeqType,
        seqOp2: SeqType,
        op: IfButtonOp,
        spot: SpotanimType? = null,
    ) {
        val seq = if (op == IfButtonOp.Op2) seqOp2 else seqOp1
        simpleAnim(seq, spot)
    }

    private suspend fun ProtectedAccess.lockedAnimDialog(
        seq: SeqType,
        varbit: VarBitType,
        text: String,
        varbitStateReq: Int = 1,
        spot: SpotanimType? = null,
    ) {
        val state = vars[varbit]
        if (state < varbitStateReq) {
            mesbox(text)
            return
        }
        simpleAnim(seq, spot)
    }

    private suspend fun ProtectedAccess.lockedLoopAnimDialog(
        seqOp1: SeqType,
        seqOp2: SeqType,
        varbit: VarBitType,
        text: String,
        spot: SpotanimType? = null,
        op: IfButtonOp,
        varbitStateReq: Int = 1,
    ) {
        val state = vars[varbit]
        if (state < varbitStateReq) {
            mesbox(text)
            return
        }
        loopAnim(seqOp1, seqOp2, op, spot)
    }

    private suspend fun ProtectedAccess.skillCapeEmote() {
        stopAction()

        if (ocIsType(player.back, objs.music_cape, objs.music_cape_t)) {
            masteryCapeEmote(seqs.emote_air_guitar, spotanims.air_guitar_spotanim)
            return
        }

        if (ocIsType(player.back, objs.quest_point_cape, objs.quest_point_cape_t)) {
            val seq = seqs.skillcapes_player_quest_cape
            val spot = spotanims.skillcapes_quest_cape_spotanim
            masteryCapeEmote(seq, spot)
            return
        }

        if (ocIsType(player.back, objs.achievement_diary_cape, objs.achievement_diary_cape_t)) {
            achievementDiaryCapeEmote()
            return
        }

        if (ocIsContentType(player.back, content.max_cape)) {
            masteryCapeEmote(seqs.max_cape_player_anim, spotanims.max_cape)
            return
        }

        val skillCape = ocIsContentType(player.back, content.skill_cape)
        if (!skillCape) {
            mes("You need to be wearing a skillcape in order to perform that emote.")
            return
        }

        val stat = ocParamOrNull(player.back, params.statreq1_skill)
        if (stat == null || stat !in skillCapeEmotes) {
            mes("You need to be wearing a skillcape in order to perform that emote.")
            return
        }

        val (anim, spotanim) = skillCapeEmotes[stat]
        masteryCapeEmote(anim, spotanim)
    }

    private suspend fun ProtectedAccess.masteryCapeEmote(seq: SeqType, spotanim: SpotanimType) {
        if (isInCombat()) {
            mes("You can't perform that emote now.")
            return
        }
        playAnim(seq, spotanim)
        delay(seq)
        rebuildAppearance()
    }

    private suspend fun ProtectedAccess.achievementDiaryCapeEmote() {
        if (isInCombat()) {
            mes("You can't perform that emote now.")
            return
        }
        val southWest = coords.translate(Direction.SouthWest)
        val npc =
            Npc(npcTypes[npcs.diary_emote_npc], southWest).apply {
                respawnDir = Direction.South
                mode = NpcMode.None
            }

        val validLineOfWalk = lineOfWalk(coords, npc.bounds())
        if (!validLineOfWalk) {
            mes("You can't do this emote here.")
            return
        }

        anim(seqs.diary_emote_playeranim)
        faceEntitySquare(npc)
        npcRepo.add(npc, duration = 30)
        npc.anim(seqs.diary_emote_spotanim)
        delay(31)
        rebuildAppearance()
    }

    private fun ProtectedAccess.airGuitarEmote() {
        stopAction()
        val unlocked = vars[varbits.emote_musiccape] != 0
        if (!unlocked) {
            mes(
                "You need to have bought a music cape and have all music tracks " +
                    "unlocked (apart from holiday events) in order to perform that emote."
            )
            return
        }
        if (isInCombat()) {
            mes("You can't perform that emote now.")
            return
        }
        midiJingle(jingles.emote_air_guitar)
        playAnim(seqs.emote_air_guitar, spotanims.air_guitar_spotanim)
    }

    private suspend fun ProtectedAccess.uriTransformEmote() {
        stopAction()
        val unlocked = vars[varbits.emote_uri_transform] != 0
        if (!unlocked) {
            mesbox("This emote can be unlocked by completing at least 300 hard clue scrolls.")
            return
        }
        if (isInCombat()) {
            mes("You can't perform that emote now.")
            return
        }
        spotanim(spotanims.smokepuff, height = 92)
        transmog(npcs.uri_emote_1)
        delay(1)
        spotanim(spotanims.briefcase_spotanim)
        anim(seqs.emote_uri_briefcase)
        transmog(npcs.uri_emote_2)
        delay(9)
        anim(seqs.poh_smash_magic_tablet)
        delay(1)
        spotanim(spotanims.poh_absorb_tablet_magic)
        anim(seqs.poh_absorb_tablet_teleport)
        delay(1)
        spotanim(spotanims.smokepuff, height = 92)
        resetAnim()
        resetTransmog()
        publishEmoteEvent(seqs.emote_uri_briefcase)
    }

    private suspend fun ProtectedAccess.premierShieldEmote() {
        stopAction()
        if (vars[varbits.emote_premier_club_2018] < 1) {
            mesbox("This emote is unlocked upon creating an account.")
            return
        }

        if (mapClock - player.premierShieldClock < 4) {
            mes("You're already doing that.")
            return
        }

        val spot =
            when (player.premierShieldCount) {
                1 -> spotanims.premier_club_emote_spotanim_silver
                2 -> spotanims.premier_club_emote_spotanim_gold
                else -> spotanims.premier_club_emote_spotanim_bronze
            }

        player.premierShieldCount = (player.premierShieldCount + 1) % 3
        player.premierShieldClock = mapClock
        playAnim(seqs.premier_club_emote, spot)
    }

    private suspend fun ProtectedAccess.relicUnlockEmote() {
        stopAction()
        val unlocked = vars[varbits.poh_leaguehall_outfitstand_relichunter_type] != 0
        if (!unlocked) {
            mesbox(
                "You can't use that emote unless you have stored a " +
                    "tier 3 relichunter outfit on the outfitstand in your " +
                    "player owned house League Hall."
            )
            return
        }
        if (isInCombat()) {
            mes("You can't perform that emote now.")
            return
        }
        val seq = if (isBodyTypeB()) seqs.human_relic_unlock_female else seqs.human_relic_unlock
        anim(seq)
        if (vars[varbits.poh_leaguehall_outfitstand_relichunter_type] == 3) {
            spotanim(spotanims.league_twisted_relic_unlock_spot, height = 92)
        } else {
            spotanim(spotanims.league_trailblazer_relic_unlock_spot, height = 92)
        }
        delay(4)
        publishEmoteEvent(seq)
    }

    private suspend fun ProtectedAccess.fortisSaluteEmote(loop: Boolean) {
        val unlocked = vars[varps.colosseum_glory] >= 20_000
        if (!unlocked) {
            mesbox(
                "This emote is unlocked by reaching <col=ff0000>" +
                    "Grand Champion</col> status in the Fortis Colosseum."
            )
            return
        }
        val seq = if (loop) seqs.emote_varlamore_salute_loop else seqs.emote_varlamore_salute
        simpleAnim(seq)
    }

    private fun ProtectedAccess.publishEmoteEvent(seq: SeqType) {
        val event = PlayEmote(player, seq)
        eventBus.publish(event)
    }

    private fun Player.onTabOpen() {
        ifSetEvents(
            emote_components.emote_list,
            emoteSlotRange,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
        )
    }

    private fun ProtectedAccess.flapEmoteSelector(op: IfButtonOp): SeqType {
        val chickenPieces = invTotal(worn, content.chicken_outfit)
        return if (chickenPieces >= 4) {
            seqs.vm_natural_historian_monkey_hop
        } else {
            if (op == IfButtonOp.Op2) seqs.emote_panic_flap_loop else seqs.emote_panic_flap
        }
    }

    private fun ProtectedAccess.crazyDanceEmoteSelector(op: IfButtonOp): SeqType {
        player.crazyDanceCount = (player.crazyDanceCount + 1) % 2
        return if (player.crazyDanceCount == 0) {
            if (op == IfButtonOp.Op2) seqs.bday17_style_loop else seqs.bday17_style
        } else {
            if (op == IfButtonOp.Op2) seqs.bday17_lasso_loop else seqs.bday17_lasso
        }
    }

    private fun SeqType.requiresWalkTrigger(): Boolean = seqTypes[this].replayCount == 255
}

private class SkillCapeEmoteResolver
@Inject
constructor(private val enumResolver: EnumTypeMapResolver) {
    private lateinit var skillCapeAnims: EnumTypeMap<StatType, SeqType>
    private lateinit var skillCapeSpots: EnumTypeMap<StatType, SpotanimType>

    fun startup() {
        skillCapeAnims = enumResolver[emote_enums.skill_cape_anims]
        skillCapeSpots = enumResolver[emote_enums.skill_cape_spots]
    }

    operator fun contains(stat: StatType): Boolean =
        stat in skillCapeAnims && stat in skillCapeAnims

    operator fun get(stat: StatType): Pair<SeqType, SpotanimType> {
        val anim = checkNotNull(skillCapeAnims[stat]) { "Skill cape anim not defined for: $stat" }
        val spot = checkNotNull(skillCapeSpots[stat]) { "Skill cape spot not defined for: $stat" }
        return anim to spot
    }
}

private var Player.premierShieldClock by intVarp(emote_varps.emote_clock_premier_shield)
private var Player.premierShieldCount by intVarBit(emote_varbits.emote_counters_premier_shield)
private var Player.crazyDanceCount by intVarBit(emote_varbits.emote_counters_crazy_dance)
