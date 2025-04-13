package org.rsmod.game.queue

public enum class EngineQueueType(public val id: Int) {
    ChangeStat(1),
    AdvanceStat(2),
    Mapzone(3),
    MapzoneExit(4),
    Zone(5),
    ZoneExit(6),
}
