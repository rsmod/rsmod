package org.rsmod.api.music.plugin.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.music.plugin.configs.music_timers
import org.rsmod.api.player.music.MusicPlayer
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onPlayerLogin
import org.rsmod.api.script.onPlayerSoftTimer
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class MusicTimerScript @Inject constructor(private val musicPlayer: MusicPlayer) :
    PluginScript() {
    private val Player.musicVolume by intVarp(varps.option_music)
    private var Player.currMusicId by intVarBit(varbits.music_curr_id)
    private var Player.musicClock by intVarBit(varbits.music_curr_clocks)
    private var Player.musicDuration by intVarBit(varbits.music_curr_duration)

    override fun ScriptContext.startup() {
        onPlayerLogin { player.musicLogin() }
        onPlayerSoftTimer(music_timers.sync) { player.musicSync() }
    }

    private fun Player.musicLogin() {
        // TODO(emulation): Music is meant to start playing (sent twice as well) on the first
        //  login cycle for the player. However, as of now, the music area id is assigned when
        //  the engine queue `onarea` is invoked, which does not happen until the second cycle
        //  that the player is logged in for (due to when player logins take place in the loop).
        //  There are a few workarounds we can do, which includes directly accessing the `AreaIndex`
        //  and finding the music for each area; however, this seems excessive for this use case.
        //  We will leave this as-is and decide at a later date.
        softTimer(music_timers.sync, cycles = 1)
    }

    private fun Player.musicSync() {
        if (isMusicMuted()) {
            return
        }

        // TODO(emulation): This is meant to have an extra 1-cycle delay before playing the next
        //  music track. In total it should take 2 cycles when going from "stop midi" to playing
        //  the next midi.
        if (currMusicId == 0) {
            musicPlayer.playNext(this)
            return
        }

        musicClock++

        val endMusic = musicClock > musicDuration
        if (endMusic) {
            musicPlayer.stop(this)
            return
        }
    }

    private fun Player.isMusicMuted(): Boolean {
        return musicVolume == 0
    }
}
