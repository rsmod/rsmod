package org.rsmod.game.entity.npc

import org.rsmod.map.CoordGrid

public data class NpcPatrol(public val waypoints: List<NpcPatrolWaypoint>) :
    List<NpcPatrolWaypoint> by waypoints {
    public fun coordList(): List<CoordGrid> = waypoints.map { it.destination }
}

public data class NpcPatrolWaypoint(public val destination: CoordGrid, public val pauseDelay: Int)
