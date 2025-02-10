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
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
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
        onIfOpen(interfaces.emote_tab) { player.onTabOpen() }
        onIfOverlayButton(emote_components.emote_list) { player.selectEmote(comsub, op) }
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
            "Angry" -> simpleAnim(seqs.emote_beckon)
            "Think" -> simpleAnim(seqs.emote_think)
            "Wave" -> simpleAnim(seqs.emote_wave)
            "Shrug" -> simpleAnim(seqs.emote_shrug)
            "Cheer" -> simpleAnim(seqs.emote_cheer)
            "Beckon" -> simpleAnim(seqs.emote_beckon)
            "Laugh" -> simpleAnim(seqs.emote_laugh)
            "Jump for Joy" -> simpleAnim(seqs.emote_jump_with_joy)
            "Yawn" -> simpleAnim(seqs.emote_yawn)
            "Dance" -> loopAnim(seqs.emote_dance, seqs.emote_dance_loop, op)
            "Jig" -> loopAnim(seqs.emote_jig, seqs.emote_jig_loop, op)
            "Spin" -> simpleAnim(seqs.emote_spin)
            "Headbang" -> loopAnim(seqs.emote_headbang, seqs.emote_headbang_loop, op)
            "Cry" -> simpleAnim(seqs.emote_cry)
            "Blow Kiss" -> simpleAnim(seqs.emote_blow_kiss_low_prio)
            "Panic" -> simpleAnim(seqs.emote_panic)
            "Raspberry" -> simpleAnim(seqs.emote_raspberry)
            "Clap" -> simpleAnim(seqs.emote_clap)
            "Salute" -> simpleAnim(seqs.emote_salute)
            "Goblin Bow" ->
                lockedAnimDialog(
                    seqs.emote_goblin_bow,
                    varbits.lost_tribe_progress,
                    "This emote can be unlocked during the Lost Tribe quest.",
                    varbitStateReq = 7,
                )
            "Goblin Salute" ->
                lockedAnimDialog(
                    seqs.emote_goblin_salute,
                    varbits.lost_tribe_progress,
                    "This emote can be unlocked during the Lost Tribe quest.",
                    varbitStateReq = 7,
                )
            "Glass Box" ->
                lockedAnimDialog(
                    seqs.emote_glass_box,
                    varbits.glass_box_emote,
                    "This emote can be unlocked during the mime random event.",
                )
            "Climb Rope" ->
                lockedAnimDialog(
                    seqs.emote_climb_rope,
                    varbits.climb_rope_emote,
                    "This emote can be unlocked during the mime random event.",
                )
            "Lean" ->
                lockedLoopAnimDialog(
                    seqs.emote_lean,
                    seqs.emote_lean_loop,
                    varbits.lean_emote,
                    "This emote can be unlocked during the mime random event.",
                    op,
                )
            "Glass Wall" ->
                lockedAnimDialog(
                    seqs.emote_glass_wall,
                    varbits.glass_wall_emote,
                    "This emote can be unlocked during the mime random event.",
                )
            "Idea" ->
                lockedAnimDialog(
                    seqs.emote_lightbulb,
                    varbits.idea_emote,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                    spot = spotanims.idea_emote,
                )
            "Stamp" ->
                lockedAnimDialog(
                    seqs.emote_stampfeet,
                    varbits.stamp_emote,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                    spot = spotanims.stampfeet_emote,
                )
            "Flap" ->
                lockedAnimDialog(
                    flapEmoteSelector(),
                    varbits.flap_emote,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                )
            "Slap Head" ->
                lockedAnimDialog(
                    seqs.emote_lightbulb,
                    varbits.idea_emote,
                    "You can't use that emote yet - visit the Stronghold of Security to unlock it.",
                )
            "Zombie Walk" ->
                lockedAnimDialog(
                    seqs.emote_zombie_walk,
                    varbits.zombie_walk_emote,
                    "This emote can be unlocked during the gravedigger random event.",
                )
            "Zombie Dance" ->
                lockedAnimDialog(
                    seqs.emote_zombie_dance,
                    varbits.zombie_dance_emote,
                    "This emote can be unlocked during the gravedigger random event.",
                )
            "Scared" ->
                lockedAnimDialog(
                    seqs.emote_scared,
                    varbits.scared_emote,
                    "This emote can be unlocked by doing a Halloween seasonal event.",
                )
            "Rabbit Hop" ->
                lockedAnimDialog(
                    seqs.emote_rabbit_hop,
                    varbits.rabbit_hop_emote,
                    "This emote can be unlocked by doing an Easter seasonal event.",
                )
            "Sit up" ->
                lockedAnimDialog(
                    seqs.emote_sit_up,
                    varbits.drill_demon_emotes,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                )
            "Push up" ->
                lockedAnimDialog(
                    seqs.emote_push_up,
                    varbits.drill_demon_emotes,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                )
            "Star jump" ->
                lockedAnimDialog(
                    seqs.emote_star_jump,
                    varbits.drill_demon_emotes,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                )
            "Jog" ->
                lockedAnimDialog(
                    seqs.emote_jog,
                    varbits.drill_demon_emotes,
                    "You can't use that emote yet - complete the Drill Demon event to unlock them.",
                )
            "Flex" ->
                lockedAnimDialog(
                    seqs.emote_flex,
                    varbits.flex_emote,
                    "You can unlock this emote by completing Checkal's task in Below Ice Mountain.",
                )
            "Zombie Hand" ->
                lockedAnimDialog(
                    seqs.emote_zombie_hand,
                    varbits.zombie_hand_emote,
                    "This emote can be unlocked by doing a Halloween seasonal event.",
                )
            "Hypermobile Drinker" ->
                lockedAnimDialog(
                    seqs.emote_hypermobile_drinker,
                    varbits.hypermobile_drinker_emote,
                    "This emote can be unlocked by doing a Halloween seasonal event.",
                )
            "Skill Cape" -> skillCapeEmote()
            "Air Guitar" -> airGuitarEmote()
            "Uri transform" -> uriTransformEmote()
            "Smooth dance" ->
                lockedAnimDialog(
                    seqs.emote_smooth_dance,
                    varbits.smooth_dance_emote,
                    "This emote can be unlocked by doing a birthday event.",
                )
            "Crazy dance" ->
                lockedAnimDialog(
                    crazyDanceEmoteSelector(),
                    varbits.crazy_dance_emote,
                    "This emote can be unlocked by doing a birthday event.",
                )
            "Premier Shield" -> premierShieldEmote()
            "Explore" ->
                lockedAnimDialog(
                    seqs.emote_explore,
                    varbits.explore_emote,
                    "This emote can be unlocked by completing at least 600 beginner clue scrolls.",
                )
            "Relic unlock" -> relicUnlockEmote()
            "Party" ->
                lockedAnimDialog(
                    seqs.emote_party,
                    varbits.party_emote,
                    "This emote can be unlocked by doing a birthday event.",
                    spot = spotanims.emote_party,
                )
            "Trick" ->
                lockedAnimDialog(
                    seqs.emote_trick,
                    varbits.trick_emote,
                    "This emote can be unlocked by doing a Halloween event.",
                    spot = spotanims.emote_trick,
                )
            "Fortis Salute" -> fortisSaluteEmote(loop = op == IfButtonOp.Op2)
            "Crab dance" -> {
                /* Emote is not available in tab. */
            }
            "Sit down" -> loopAnim(seqs.emote_sit_down_loop, seqs.emote_sit_down, op)
            else -> throw NotImplementedError("Emote not implemented: $emote")
        }
    }

    private fun ProtectedAccess.playAnim(seq: SeqType, spot: SpotanimType?) {
        anim(seq)
        spot?.let(::spotanim)
        publishEmoteEvent(seq)
    }

    private fun ProtectedAccess.simpleAnim(seq: SeqType, spot: SpotanimType? = null) {
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
            masteryCapeEmote(seqs.emote_air_guitar, spotanims.air_guitar_emote)
            return
        }

        if (ocIsType(player.back, objs.quest_point_cape, objs.quest_point_cape_t)) {
            masteryCapeEmote(seqs.quest_point_cape, spotanims.quest_point_cape)
            return
        }

        if (ocIsType(player.back, objs.achievement_diary_cape, objs.achievement_diary_cape_t)) {
            achievementDiaryCapeEmote()
            return
        }

        if (ocIsContentType(player.back, content.max_cape)) {
            masteryCapeEmote(seqs.max_cape, spotanims.max_cape)
            return
        }

        val skillCape = ocIsContentType(player.back, content.skill_cape)
        if (!skillCape) {
            mes("You need to be wearing a skillcape in order to perform that emote.")
            return
        }

        val stat = ocParam(player.back, params.statreq1_skill)
        if (stat == null || stat !in skillCapeEmotes) {
            mes("You need to be wearing a skillcape in order to perform that emote.")
            return
        }

        val (anim, spotanim) = skillCapeEmotes[stat]
        masteryCapeEmote(anim, spotanim)
    }

    private suspend fun ProtectedAccess.masteryCapeEmote(seq: SeqType, spotanim: SpotanimType) {
        // TODO: Under combat check ("You can't perform that emote now.")

        playAnim(seq, spotanim)
        delay(seq)
    }

    private suspend fun ProtectedAccess.achievementDiaryCapeEmote() {
        // TODO: Under combat check ("You can't perform that emote now.")

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

        anim(seqs.emote_achievement_diary)
        faceEntitySquare(npc)
        npcRepo.add(npc, duration = 30)
        npc.anim(seqs.achievement_diary_cape_npc_anim)
        delay(31)
    }

    private fun ProtectedAccess.airGuitarEmote() {
        stopAction()
        val unlocked = vars[varbits.air_guitar_emote] != 0
        if (!unlocked) {
            mes(
                "You need to have bought a music cape and have all music tracks " +
                    "unlocked (apart from holiday events) in order to perform that emote."
            )
            return
        }
        // TODO(content): Under combat check ("You can't perform that emote now.")

        jingle(jingles.emote_air_guitar)
        playAnim(seqs.emote_air_guitar, spotanims.air_guitar_emote)
    }

    private suspend fun ProtectedAccess.uriTransformEmote() {
        stopAction()
        val unlocked = vars[varbits.uri_transform_emote] != 0
        if (!unlocked) {
            mesbox(
                "This emote can be unlocked by completing at least 300 hard clue scrolls.",
                lineHeight = 31,
            )
            return
        }
        // TODO(content): Under combat check ("You can't perform that emote now.")

        spotanim(spotanims.poof_disappear, height = 92)
        transmog(npcs.uri_emote_1)
        delay(1)
        spotanim(spotanims.uri_emote_start)
        anim(seqs.emote_uri_start)
        transmog(npcs.uri_emote_2)
        delay(9)
        anim(seqs.human_throwtab)
        delay(1)
        spotanim(spotanims.teletab_suck_in)
        anim(seqs.human_tabtele)
        delay(1)
        spotanim(spotanims.poof_disappear, height = 92)
        resetAnim()
        resetTransmog()
        publishEmoteEvent(seqs.emote_uri_start)
    }

    private suspend fun ProtectedAccess.premierShieldEmote() {
        stopAction()
        if (vars[varbits.premier_shield_emote] < 1) {
            mesbox("This emote is unlocked upon creating an account.", lineHeight = 31)
            return
        }

        if (mapClock - player.premierShieldClock < 4) {
            mes("You're already doing that.")
            return
        }

        val spot =
            when (player.premierShieldCount++) {
                1 -> spotanims.premier_shield_emote_silver
                2 -> spotanims.premier_shield_emote_gold
                else -> spotanims.premier_shield_emote_bronze
            }

        player.premierShieldClock = mapClock
        playAnim(seqs.emote_premier_shield, spot)
    }

    private suspend fun ProtectedAccess.relicUnlockEmote() {
        stopAction()
        val unlocked = vars[varbits.relic_unlock_emote] != 0
        if (!unlocked) {
            mes(
                "You can't use that emote unless you have stored a " +
                    "tier 3 relichunter outfit on the outfitstand in your " +
                    "player owned house League Hall."
            )
            return
        }
        // TODO: Under combat check ("You can't perform that emote now.")

        anim(seqs.emote_relic_unlock)
        if (vars[varbits.relic_unlock_emote] == 3) {
            spotanim(spotanims.twisted_relic_unlock, height = 92)
        } else {
            spotanim(spotanims.trailblazer_relic_unlock, height = 92)
        }
        delay(4)
        publishEmoteEvent(seqs.emote_relic_unlock)
    }

    private suspend fun ProtectedAccess.fortisSaluteEmote(loop: Boolean) {
        val unlocked = vars[varps.fortis_colosseum_glory_highscore] >= 20_000
        if (!unlocked) {
            mesbox(
                "This emote is unlocked by reaching <col=ff0000>" +
                    "Grand Champion</col> status in the Fortis Colosseum.",
                lineHeight = 31,
            )
            return
        }
        val seq = if (loop) seqs.emote_fortis_salute_loop else seqs.emote_fortis_salute
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
            seqs.emote_flap_chicken_outfit
        } else {
            seqs.emote_flap
        }
    }

    private fun ProtectedAccess.crazyDanceEmoteSelector(): SeqType {
        return if (player.crazyDanceCount++ % 2 == 0) {
            seqs.emote_crazy_dance2
        } else {
            seqs.emote_crazy_dance1
        }
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
private var Player.premierShieldCount by intVarp(emote_varbits.emote_counters_premier_shield)
private var Player.crazyDanceCount by intVarp(emote_varbits.emote_counters_crazy_dance)
