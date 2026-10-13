package org.runerealm.api.music.plugin.configs

import org.runerealm.api.type.refs.timer.TimerReferences
import org.runerealm.game.type.timer.TimerType

public typealias music_timers = MusicTimers

public object MusicTimers : TimerReferences() {
    public val sync: TimerType = find("music_sync")
}
