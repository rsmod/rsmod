package org.rsmod.api.music.plugin.configs

import org.rsmod.api.type.refs.timer.TimerReferences
import org.rsmod.game.type.timer.TimerType

public typealias music_timers = MusicTimers

public object MusicTimers : TimerReferences() {
    public val sync: TimerType = find("music_sync")
}
