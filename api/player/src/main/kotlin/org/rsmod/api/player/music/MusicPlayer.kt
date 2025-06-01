package org.rsmod.api.player.music

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.midis
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.music.Music
import org.rsmod.api.music.MusicRepository
import org.rsmod.api.player.chatMesColorTag
import org.rsmod.api.player.midiSong
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.player.vars.VarPlayerIntMapSetter
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.enumVarBit
import org.rsmod.api.player.vars.enumVarp
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Player
import org.rsmod.game.type.area.AreaType
import org.rsmod.game.type.area.AreaTypeList
import org.rsmod.game.type.dbrow.DbRowType

public class MusicPlayer
@Inject
internal constructor(
    private val random: GameRandom,
    private val repo: MusicRepository,
    private val areaTypes: AreaTypeList,
) {
    private val Player.playMode by enumVarp<MusicPlayMode>(varps.musicplay)
    private val Player.areaMode by enumVarBit<MusicAreaMode>(varbits.music_area_mode)

    private var Player.lastMusicId by intVarBit(varbits.music_last_id)
    private var Player.currMusicId by intVarBit(varbits.music_curr_id)
    private var Player.currMusicArea by intVarBit(varbits.music_curr_area)

    private var Player.musicClocks by intVarBit(varbits.music_curr_clocks)
    private var Player.musicDuration by intVarBit(varbits.music_curr_duration)

    private var Player.musicPlaylist by intVarp(varps.music_playlist)
    private var Player.unlockMessageDisabled by boolVarBit(varbits.music_unlock_text_toggle)

    public fun unlockAndPlay(player: Player, musicRow: DbRowType) {
        val music = getUnlockableOrThrow(musicRow)
        unlockAndPlay(player, music)
    }

    private fun unlockAndPlay(player: Player, music: Music) {
        unlock(player, music)
        play(player, music)
    }

    public fun play(player: Player, musicRow: DbRowType) {
        val music = getOrThrow(musicRow)
        play(player, music)
    }

    private fun play(player: Player, music: Music) {
        val currMusicId = player.currMusicId

        // Attempting to play music that is already playing does not restart the midi.
        val alreadyPlaying = currMusicId == music.id
        if (!alreadyPlaying) {
            player.musicClocks = 0
            player.musicDuration = music.duration
        }
        player.currMusicId = music.id

        val fadeSpeed = if (currMusicId == 0) 0 else MUSIC_PLAY_FADE
        player.midiSong(music.midi, fadeOutSpeed = fadeSpeed, fadeInDelay = fadeSpeed)
        player.ifSetText(components.music_now_playing_text, music.displayName)
    }

    public fun resume(player: Player) {
        val music = repo.forId(player.currMusicId)
        if (music == null) {
            stop(player)
            setEmptyMusicText(player)
            return
        }
        player.currMusicId = 0 // Sends the midi song without fading.
        play(player, music)
    }

    public fun stop(player: Player) {
        player.lastMusicId = player.currMusicId
        player.currMusicId = 0
        player.musicClocks = 0
        player.musicDuration = 0
        player.midiSong(midis.stop_music, fadeOutSpeed = MUSIC_END_FADE)
        if (player.playMode == MusicPlayMode.Manual) {
            setEmptyMusicText(player)
        }
    }

    public fun unlock(player: Player, musicRow: DbRowType) {
        val music = getUnlockableOrThrow(musicRow)
        unlock(player, music)
    }

    private fun unlock(player: Player, music: Music) {
        val varp = music.unlockVarp ?: error("Music cannot be unlocked: '${music.displayName}'")
        if (hasUnlocked(player, music)) {
            return
        }
        val unlockValue = player.vars[varp] or music.unlockBitflag
        player.sendUnlockMessage(music)
        VarPlayerIntMapSetter.set(player, varp, unlockValue)
    }

    public fun hasUnlocked(player: Player, musicRow: DbRowType): Boolean {
        val music = getOrThrow(musicRow)
        return hasUnlocked(player, music)
    }

    private fun hasUnlocked(player: Player, music: Music): Boolean {
        val varp = music.unlockVarp ?: return false
        val unlockedValue = player.vars[varp] and music.unlockBitflag
        return unlockedValue != 0
    }

    private fun setEmptyMusicText(player: Player) {
        player.ifSetText(components.music_now_playing_text, " ")
    }

    private fun Player.sendUnlockMessage(music: Music) {
        if (unlockMessageDisabled) {
            return
        }
        val color = chatMesColorTag(opaque = "e00a19", transparent = "ff3045")
        mes("You have unlocked a new music track: $color${music.displayName}")
    }

    private fun getUnlockableOrThrow(row: DbRowType): Music {
        val music = getOrThrow(row)
        if (music.unlockVarp == null) {
            error("This music can be played but not unlocked: '${music.displayName}'")
        }
        return music
    }

    private fun getOrThrow(row: DbRowType): Music {
        return repo.forRow(row) ?: error("DbRow is not a valid music row: '${row.internalName}'")
    }

    public fun playNext(player: Player) {
        when (player.playMode) {
            MusicPlayMode.Area -> playNextArea(player)
            MusicPlayMode.Random -> playNextRandom(player)
            MusicPlayMode.Manual -> {}
        }
    }

    private fun playNextArea(player: Player) {
        if (player.currMusicArea == 0) {
            return
        }
        val area = areaTypes.getValue(player.currMusicArea - 1)
        when (player.areaMode) {
            MusicAreaMode.Modern -> {
                val modernMusic = repo.getModernArea(area)
                if (modernMusic != null) {
                    // TODO(emulation): Does this unlock all tracks if they haven't already? Or
                    //  only the shuffled track that is selected to play? Will need to enter an
                    //  area with 3+ music tracks in classic mode, then switch to modern and wait
                    //  for the classic track to complete and see if the rest unlock all in one go
                    //  or one at a time as they play.
                    playShuffled(player, modernMusic)
                }
            }
            MusicAreaMode.Classic -> {
                val classicMusic = repo.getClassicArea(area)
                if (classicMusic != null && player.lastMusicId != classicMusic.id) {
                    unlockAndPlay(player, classicMusic)
                } else {
                    setEmptyMusicText(player)
                }
            }
        }
    }

    private fun playNextRandom(player: Player) {
        val unlocked =
            repo.getAll().filter { hasUnlocked(player, it) && it.id != player.currMusicId }
        val random = random.pickOrNull(unlocked) ?: repo.getAll().first()
        play(player, random)
    }

    public fun enterArea(player: Player, area: AreaType) {
        player.musicPlaylist = 0
        when (player.areaMode) {
            MusicAreaMode.Modern -> {
                val modernMusic = repo.getModernArea(area)
                if (modernMusic != null) {
                    player.currMusicArea = area.id + 1
                    unlockAndPlayShuffled(player, modernMusic)
                }
            }
            // Note: When entering a modern area with classic music mode, it does _not_ unlock
            // the modern tracks.
            MusicAreaMode.Classic -> {
                val classicMusic = repo.getClassicArea(area)
                if (classicMusic != null) {
                    player.currMusicArea = area.id + 1
                    unlockAndPlay(player, classicMusic)
                }
            }
        }
    }

    private fun unlockAndPlayShuffled(player: Player, musicList: List<Music>) {
        for (music in musicList) {
            unlock(player, music)
        }
        if (player.playMode == MusicPlayMode.Area) {
            playShuffled(player, musicList)
        }
    }

    // Note: This uses a seeded random "music playlist" to keep a shuffled list of music tracks
    // when using the modern music area mode. This playlist will always keep the first music track
    // the same and shuffle the rest.
    private fun playShuffled(player: Player, musicList: List<Music>) {
        if (player.musicPlaylist == 0) {
            player.musicPlaylist = MusicPlaylist.create(random).packed
        }
        val playlist = MusicPlaylist(player.musicPlaylist)
        val music = playlist.getShuffledTrack(musicList)
        unlockAndPlay(player, music)

        val next = playlist.nextPosition(musicList.size)
        player.musicPlaylist = next.packed
    }

    public companion object {
        public const val MUSIC_PLAY_FADE: Int = 60
        public const val MUSIC_END_FADE: Int = 20
    }
}
