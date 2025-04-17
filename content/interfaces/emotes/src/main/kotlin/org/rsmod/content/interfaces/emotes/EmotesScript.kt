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
    private val skillCapeEmotes: SkillCapeEmoteResolver,
) : PluginScript() {
    private lateinit var emoteSlots: EnumTypeMap<Int, String>
    private lateinit var emoteSlotRange: IntRange

    override fun ScriptContext.startUp() {
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
        skillCapeEmotes.startUp()
    }

    private fun Player.selectEmote(emoteSlot: Int, op: IfButtonOp) {
        val emote = emoteSlots.getValue(emoteSlot)
        ifClose(eventBus)
        protectedAccess.launch(this) { selectEmote(emote, op) }
    }

    private suspend fun ProtectedAccess.selectEmote(emote: String, op: IfButtonOp) {
        when (emote) {
            "Yes" -> simpleAnim(seqs.emote_yes)
            "No" -> simpleAnim(seqs.emote_no)
            "Bow" -> simpleAnim(seqs.emote_bow)
            "Angry" -> simpleAnim(seqs.emote_angry)
            "Think" -> simpleAnim(seqs.emote_think)
            "Wave" -> simpleAnim(seqs.emote_wave)
            "Shrug" -> simpleAnim(seqs.emote_shrug)
            "Cheer" -> simpleAnim(seqs.emote_cheer)
            "Beckon" -> simpleAnim(seqs.emote_beckon)
            "Laugh" -> simpleAnim(seqs.emote_laugh)
            "Jump for Joy" -> simpleAnim(seqs.emote_jump_with_joy)
            "Yawn" -> simpleAnim(seqs.emote_yawn)
            "Dance" -> loopAnim(seqs.emote_dance, seqs.emote_dance_loop, op)
            "Jig" -> loopAnim(seqs.emote_dance_scottish, seqs.emote_dance_scottish_loop, op)
            "Spin" -> simpleAnim(seqs.emote_dance_spin)
            "Headbang" -> loopAnim(seqs.emote_dance_headbang, seqs.emote_dance_headbang_loop, op)
            "Cry" -> simpleAnim(seqs.emote_cry)
            "Blow Kiss" -> simpleAnim(seqs.emote_blow_kiss)
            "Panic" -> simpleAnim(seqs.emote_panic)
            "Raspberry" -> simpleAnim(seqs.emote_ya_boo_sucks)
            "Clap" -> simpleAnim(seqs.emote_clap)
            "Salute" -> simpleAnim(seqs.emote_fremmenik_salute)
            "Goblin Bow" ->
                lockedAnimDialog(
                    seqs.human_cave_goblin_bow,
                    varbits.lost_tribe_progress,
                    "This emote can be unlocked during the Lost Tribe quest.",
                    varbitStateReq = 7,
                )
            "Goblin Salute" ->
                lockedAnimDialog(
                    seqs.human_cave_goblin_dance,
                    varbits.lost_tribe_progress,
                    "This emote can be unlocked during the Lost Tribe quest.",
                    varbitStateReq = 7,
                )
            "Glass Box" ->
                lockedAnimDialog(
                    seqs.emote_glass_box,
                    varbits.emote_glassbox,
                    "This emote can be unlocked during the mime random event.",
                )
            "Climb Rope" ->
                lockedAnimDialog(
                    seqs.emote_climbing_rope,
                    varbits.emote_climbrope,
                    "This emote can be unlocked during the mime random event.",
                )
            "Lean" ->
                lockedLoopAnimDialog(
                    seqs.emote_mime_lean,
                    seqs.emote_mime_lean_loop,
                    varbits.emote_lean,
                    "This emote can be unlocked during the mime random event.",
                    op,
                )
            "Glass Wall" ->
                lockedAnimDialog(
                    seqs.emote_glass_wall,
                    varbits.emote_glasswall,
                    "This emote can be unlocked during the mime random event.",
                )
            "Idea" ->
                lockedAnimDialog(
                    seqs.emote_lightbulb,
                    varbits.sos_emote_idea,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                    spot = spotanims.emote_lightbulb_spot,
                )
            "Stamp" ->
                lockedAnimDialog(
                    seqs.emote_stampfeet,
                    varbits.sos_emote_stamp,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                    spot = spotanims.emote_duststamp_spot,
                )
            "Flap" ->
                lockedAnimDialog(
                    flapEmoteSelector(),
                    varbits.sos_emote_flap,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                )
            "Slap Head" ->
                lockedAnimDialog(
                    seqs.emote_slap_head,
                    varbits.sos_emote_idea,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                )
            "Zombie Walk" ->
                lockedAnimDialog(
                    seqs.zombie_walk_emote,
                    varbits.emote_zombie_walk,
                    "This emote can be unlocked during the gravedigger random event.",
                )
            "Zombie Dance" ->
                lockedAnimDialog(
                    seqs.zombie_dance,
                    varbits.emote_zombie_dance,
                    "This emote can be unlocked during the gravedigger random event.",
                )
            "Scared" ->
                lockedAnimDialog(
                    seqs.terrified_emote,
                    varbits.emote_terrified,
                    "This emote can be unlocked by doing a Halloween seasonal event.",
                )
            "Rabbit Hop" ->
                lockedAnimDialog(
                    seqs.rabbit_emote,
                    varbits.emote_bunny_hop,
                    "This emote can be unlocked by doing an Easter seasonal event.",
                )
            "Sit up" ->
                lockedAnimDialog(
                    seqs.emote_situps_5,
                    varbits.emote_drilldemon,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                )
            "Push up" ->
                lockedAnimDialog(
                    seqs.emote_pushups_5,
                    varbits.emote_drilldemon,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                )
            "Star jump" ->
                lockedAnimDialog(
                    seqs.emote_starjump_5,
                    varbits.emote_drilldemon,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                )
            "Jog" ->
                lockedAnimDialog(
                    seqs.emote_run_on_spot,
                    varbits.emote_drilldemon,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                )
            "Flex" ->
                lockedAnimDialog(
                    seqs.emote_flex,
                    varbits.emote_flex,
                    "You can unlock this emote by completing Checkal's task in Below Ice Mountain.",
                )
            "Zombie Hand" ->
                lockedAnimDialog(
                    seqs.hw07_arm_from_the_ground_emote,
                    varbits.emote_zombie_hand,
                    "This emote can be unlocked by doing a Halloween seasonal event.",
                )
            "Hypermobile Drinker" ->
                lockedAnimDialog(
                    seqs.ash_emote,
                    varbits.emote_ash,
                    "This emote can be unlocked by doing a Halloween seasonal event.",
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
                    crazyDanceEmoteSelector(),
                    varbits.emote_gangnam,
                    "This emote can be unlocked by doing a birthday event.",
                )
            "Premier Shield" -> premierShieldEmote()
            "Explore" ->
                lockedAnimDialog(
                    seqs.emote_explore,
                    varbits.emote_explore,
                    "This emote can be unlocked by completing at least 600 beginner clue scrolls.",
                )
            "Relic unlock" -> relicUnlockEmote()
            "Party" ->
                lockedAnimDialog(
                    seqs.emote_party,
                    varbits.emote_party,
                    "This emote can be unlocked by doing a birthday event.",
                    spot = spotanims.fx_emote_party01_active,
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

    private fun ProtectedAccess.loopAnim(seqOp1: SeqType, seqOp2: SeqType, op: IfButtonOp) {
        val seq = if (op == IfButtonOp.Op2) seqOp2 else seqOp1
        simpleAnim(seq)
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
            mesbox(text, lineHeight = 31)
            return
        }
        simpleAnim(seq, spot)
    }

    private suspend fun ProtectedAccess.lockedLoopAnimDialog(
        seqOp1: SeqType,
        seqOp2: SeqType,
        varbit: VarBitType,
        text: String,
        op: IfButtonOp,
        varbitStateReq: Int = 1,
    ) {
        val state = vars[varbit]
        if (state < varbitStateReq) {
            mesbox(text, lineHeight = 31)
            return
        }
        loopAnim(seqOp1, seqOp2, op)
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
            Npc(npcTypes[npcs.achievement_diary_cape_emote], southWest).apply {
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
            mesbox(
                "This emote can be unlocked by completing at least 300 hard clue scrolls.",
                lineHeight = 31,
            )
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
            mesbox("This emote is unlocked upon creating an account.", lineHeight = 31)
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
            mes(
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
        anim(seqs.human_relic_unlock)
        if (vars[varbits.poh_leaguehall_outfitstand_relichunter_type] == 3) {
            spotanim(spotanims.league_twisted_relic_unlock_spot, height = 92)
        } else {
            spotanim(spotanims.league_trailblazer_relic_unlock_spot, height = 92)
        }
        delay(4)
        publishEmoteEvent(seqs.human_relic_unlock)
    }

    private suspend fun ProtectedAccess.fortisSaluteEmote(loop: Boolean) {
        val unlocked = vars[varps.colosseum_glory] >= 20_000
        if (!unlocked) {
            mesbox(
                "This emote is unlocked by reaching <col=ff0000>" +
                    "Grand Champion</col> status in the Fortis Colosseum.",
                lineHeight = 31,
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

    private fun ProtectedAccess.flapEmoteSelector(): SeqType {
        val chickenPieces = invTotal(worn, content.chicken_outfit)
        return if (chickenPieces >= 4) {
            seqs.vm_natural_historian_monkey_hop
        } else {
            seqs.emote_panic_flap
        }
    }

    private fun ProtectedAccess.crazyDanceEmoteSelector(): SeqType {
        player.crazyDanceCount = (player.crazyDanceCount + 1) % 2
        return if (player.crazyDanceCount == 0) {
            seqs.bday17_style
        } else {
            seqs.bday17_lasso
        }
    }

    private fun SeqType.requiresWalkTrigger(): Boolean =
        when (this) {
            seqs.emote_dance_loop,
            seqs.emote_varlamore_salute_loop,
            seqs.emote_dance_headbang_loop,
            seqs.emote_dance_scottish_loop,
            seqs.emote_mime_lean_loop,
            seqs.emote_sit_loop -> true
            else -> false
        }
}

private class SkillCapeEmoteResolver
@Inject
constructor(private val enumResolver: EnumTypeMapResolver) {
    private lateinit var skillCapeAnims: EnumTypeMap<StatType, SeqType>
    private lateinit var skillCapeSpots: EnumTypeMap<StatType, SpotanimType>

    fun startUp() {
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
