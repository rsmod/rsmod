@file:Suppress("konsist.properties are declared before functions")

package org.rsmod.api.type.script.dsl

import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.npc.NpcPatrol
import org.rsmod.game.entity.npc.NpcPatrolWaypoint
import org.rsmod.game.map.Direction
import org.rsmod.game.movement.BlockWalk
import org.rsmod.game.movement.MoveRestrict
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.util.CompactableIntArray
import org.rsmod.game.type.util.ParamMapBuilder
import org.rsmod.map.CoordGrid

@DslMarker private annotation class NpcBuilderDsl

@NpcBuilderDsl
public class NpcPluginBuilder(public var internal: String? = null) {
    private val backing: NpcTypeBuilder = NpcTypeBuilder()

    public var name: String? by backing::name
    public var desc: String? by backing::desc
    public var size: Int? by backing::size
    public var models: CompactableIntArray by backing::models
    public var readyAnim: Int? by backing::readyAnim
    public var walkAnim: Int? by backing::walkAnim
    public var turnBackAnim: Int? by backing::turnBackAnim
    public var turnLeftAnim: Int? by backing::turnLeftAnim
    public var turnRightAnim: Int? by backing::turnRightAnim
    public var category: Int? by backing::category
    public var head: CompactableIntArray by backing::head
    public var minimap: Boolean? by backing::minimap
    public var vislevel: Int? by backing::vislevel
    public var resizeH: Int? by backing::resizeH
    public var resizeV: Int? by backing::resizeV
    public var alwaysOnTop: Boolean? by backing::alwaysOnTop
    public var ambient: Int? by backing::ambient
    public var contrast: Int? by backing::contrast
    // TODO: private and use a more plugin-esque approach with graphic types and such
    public var headIconGraphic: CompactableIntArray by backing::headIconGraphic
    public var headIconIndex: CompactableIntArray by backing::headIconGraphic
    public var turnSpeed: Int? by backing::turnSpeed
    // TODO: switch to varp and varbit type
    public var multiVarp: Int? by backing::multiVarp
    public var multiVarBit: Int? by backing::multiVarBit
    // TODO: switch to npc type
    public var multiNpcDefault: Int? by backing::multiNpcDefault
    public var multiNpc: CompactableIntArray by backing::multiNpc
    public var active: Boolean? by backing::active
    public var rotationFlag: Boolean? by backing::rotationFlag
    public var follower: Boolean? by backing::follower
    public var lowPriorityOps: Boolean? by backing::lowPriorityOps
    public var overlayHeight: Int? by backing::overlayHeight
    public var runAnim: Int? by backing::runAnim
    public var runTurnBackAnim: Int? by backing::runTurnBackAnim
    public var runTurnLeftAnim: Int? by backing::runTurnLeftAnim
    public var runTurnRightAnim: Int? by backing::runTurnRightAnim
    public var crawlAnim: Int? by backing::crawlAnim
    public var crawlTurnBackAnim: Int? by backing::crawlTurnBackAnim
    public var crawlTurnLeftAnim: Int? by backing::crawlTurnLeftAnim
    public var crawlTurnRightAnim: Int? by backing::crawlTurnRightAnim
    public var param: ParamMapBuilder = ParamMapBuilder()
    public var moveRestrict: MoveRestrict? by backing::moveRestrict
    public var defaultMode: NpcMode? by backing::defaultMode
    public var blockWalk: BlockWalk? by backing::blockWalk
    public var patrolList: NpcPatrol? by backing::patrol
    public var respawnRate: Int? by backing::respawnRate
    public var maxRange: Int? by backing::maxRange
    public var wanderRange: Int? by backing::wanderRange
    public var attackRange: Int? by backing::attackRange
    public var huntRange: Int? by backing::huntRange
    public var huntMode: Int? by backing::huntMode
    public var giveChase: Boolean? by backing::giveChase
    public var attack: Int? by backing::attack
    public var strength: Int? by backing::strength
    public var defence: Int? by backing::defence
    public var hitpoints: Int? by backing::hitpoints
    public var ranged: Int? by backing::ranged
    public var magic: Int? by backing::magic
    public var timer: Int? by backing::timer
    public var respawnDir: Direction? by backing::respawnDir
    public var heroCount: Int? by backing::heroCount

    private var contentGroupId: Int? by backing::contentGroup

    public var contentGroup: ContentGroupType? = null
        set(value) {
            contentGroupId = value?.id
            field = value
        }

    private var recolS: CompactableIntArray by backing::recolS
    private var recolD: CompactableIntArray by backing::recolD
    private var retexS: CompactableIntArray by backing::retexS
    private var retexD: CompactableIntArray by backing::retexD

    private val op: Array<String?>
        get() = backing.op

    public var op1: String?
        get() = op[0]
        set(value) {
            op[0] = value
        }

    public var op2: String?
        get() = op[1]
        set(value) {
            op[1] = value
        }

    public var op3: String?
        get() = op[2]
        set(value) {
            op[2] = value
        }

    public var op4: String?
        get() = op[3]
        set(value) {
            op[3] = value
        }

    public var op5: String?
        get() = op[4]
        set(value) {
            op[4] = value
        }

    public var recol1s: Int
        get() = recolS[0]
        set(value) {
            recolS[0] = value
        }

    public var recol2s: Int
        get() = recolS[1]
        set(value) {
            recolS[1] = value
        }

    public var recol3s: Int
        get() = recolS[2]
        set(value) {
            recolS[2] = value
        }

    public var recol4s: Int
        get() = recolS[3]
        set(value) {
            recolS[3] = value
        }

    public var recol5s: Int
        get() = recolS[4]
        set(value) {
            recolS[4] = value
        }

    public var recol6s: Int
        get() = recolS[5]
        set(value) {
            recolS[5] = value
        }

    public var recol7s: Int
        get() = recolS[6]
        set(value) {
            recolS[6] = value
        }

    public var recol8s: Int
        get() = recolS[7]
        set(value) {
            recolS[7] = value
        }

    public var recol9s: Int
        get() = recolS[8]
        set(value) {
            recolS[8] = value
        }

    public var recol10s: Int
        get() = recolS[9]
        set(value) {
            recolS[9] = value
        }

    public var recol11s: Int
        get() = recolS[10]
        set(value) {
            recolS[10] = value
        }

    public var recol12s: Int
        get() = recolS[11]
        set(value) {
            recolS[11] = value
        }

    public var recol13s: Int
        get() = recolS[12]
        set(value) {
            recolS[12] = value
        }

    public var recol14s: Int
        get() = recolS[13]
        set(value) {
            recolS[13] = value
        }

    public var recol15s: Int
        get() = recolS[14]
        set(value) {
            recolS[14] = value
        }

    public var recol1d: Int
        get() = recolD[0]
        set(value) {
            recolD[0] = value
        }

    public var recol2d: Int
        get() = recolD[1]
        set(value) {
            recolD[1] = value
        }

    public var recol3d: Int
        get() = recolD[2]
        set(value) {
            recolD[2] = value
        }

    public var recol4d: Int
        get() = recolD[3]
        set(value) {
            recolD[3] = value
        }

    public var recol5d: Int
        get() = recolD[4]
        set(value) {
            recolD[4] = value
        }

    public var recol6d: Int
        get() = recolD[5]
        set(value) {
            recolD[5] = value
        }

    public var recol7d: Int
        get() = recolD[6]
        set(value) {
            recolD[6] = value
        }

    public var recol8d: Int
        get() = recolD[7]
        set(value) {
            recolD[7] = value
        }

    public var recol9d: Int
        get() = recolD[8]
        set(value) {
            recolD[8] = value
        }

    public var recol10d: Int
        get() = recolD[9]
        set(value) {
            recolD[9] = value
        }

    public var recol11d: Int
        get() = recolD[10]
        set(value) {
            recolD[10] = value
        }

    public var recol12d: Int
        get() = recolD[11]
        set(value) {
            recolD[11] = value
        }

    public var recol13d: Int
        get() = recolD[12]
        set(value) {
            recolD[12] = value
        }

    public var recol14d: Int
        get() = recolD[13]
        set(value) {
            recolD[13] = value
        }

    public var recol15d: Int
        get() = recolD[14]
        set(value) {
            recolD[14] = value
        }

    public var retex1s: Int
        get() = retexS[0]
        set(value) {
            retexS[0] = value
        }

    public var retex2s: Int
        get() = retexS[1]
        set(value) {
            retexS[1] = value
        }

    public var retex3s: Int
        get() = retexS[2]
        set(value) {
            retexS[2] = value
        }

    public var retex4s: Int
        get() = retexS[3]
        set(value) {
            retexS[3] = value
        }

    public var retex5s: Int
        get() = retexS[4]
        set(value) {
            retexS[4] = value
        }

    public var retex6s: Int
        get() = retexS[5]
        set(value) {
            retexS[5] = value
        }

    public var retex7s: Int
        get() = retexS[6]
        set(value) {
            retexS[6] = value
        }

    public var retex8s: Int
        get() = retexS[7]
        set(value) {
            retexS[7] = value
        }

    public var retex9s: Int
        get() = retexS[8]
        set(value) {
            retexS[8] = value
        }

    public var retex10s: Int
        get() = retexS[9]
        set(value) {
            retexS[9] = value
        }

    public var retex11s: Int
        get() = retexS[10]
        set(value) {
            retexS[10] = value
        }

    public var retex12s: Int
        get() = retexS[11]
        set(value) {
            retexS[11] = value
        }

    public var retex13s: Int
        get() = retexS[12]
        set(value) {
            retexS[12] = value
        }

    public var retex14s: Int
        get() = retexS[13]
        set(value) {
            retexS[13] = value
        }

    public var retex15s: Int
        get() = retexS[14]
        set(value) {
            retexS[14] = value
        }

    public var retex1d: Int
        get() = retexD[0]
        set(value) {
            retexD[0] = value
        }

    public var retex2d: Int
        get() = retexD[1]
        set(value) {
            retexD[1] = value
        }

    public var retex3d: Int
        get() = retexD[2]
        set(value) {
            retexD[2] = value
        }

    public var retex4d: Int
        get() = retexD[3]
        set(value) {
            retexD[3] = value
        }

    public var retex5d: Int
        get() = retexD[4]
        set(value) {
            retexD[4] = value
        }

    public var retex6d: Int
        get() = retexD[5]
        set(value) {
            retexD[5] = value
        }

    public var retex7d: Int
        get() = retexD[6]
        set(value) {
            retexD[6] = value
        }

    public var retex8d: Int
        get() = retexD[7]
        set(value) {
            retexD[7] = value
        }

    public var retex9d: Int
        get() = retexD[8]
        set(value) {
            retexD[8] = value
        }

    public var retex10d: Int
        get() = retexD[9]
        set(value) {
            retexD[9] = value
        }

    public var retex11d: Int
        get() = retexD[10]
        set(value) {
            retexD[10] = value
        }

    public var retex12d: Int
        get() = retexD[11]
        set(value) {
            retexD[11] = value
        }

    public var retex13d: Int
        get() = retexD[12]
        set(value) {
            retexD[12] = value
        }

    public var retex14d: Int
        get() = retexD[13]
        set(value) {
            retexD[13] = value
        }

    public var retex15d: Int
        get() = retexD[14]
        set(value) {
            retexD[14] = value
        }

    public var patrol1: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(0) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(0, value)
        }

    public var patrol2: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(1) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(1, value)
        }

    public var patrol3: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(2) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(2, value)
        }

    public var patrol4: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(3) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(3, value)
        }

    public var patrol5: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(4) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(4, value)
        }

    public var patrol6: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(5) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(5, value)
        }

    public var patrol7: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(6) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(6, value)
        }

    public var patrol8: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(7) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(7, value)
        }

    public var patrol9: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(8) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(8, value)
        }

    public var patrol10: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(9) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(9, value)
        }

    public var patrol11: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(10) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(10, value)
        }

    public var patrol12: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(11) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(11, value)
        }

    public var patrol13: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(12) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(12, value)
        }

    public var patrol14: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(13) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(13, value)
        }

    public var patrol15: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(14) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(14, value)
        }

    public var patrol16: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(15) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(15, value)
        }

    public var patrol17: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(16) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(16, value)
        }

    public var patrol18: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(17) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(17, value)
        }

    public var patrol19: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(18) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(18, value)
        }

    public var patrol20: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(19) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(19, value)
        }

    public var patrol21: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(20) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(20, value)
        }

    public var patrol22: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(21) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(21, value)
        }

    public var patrol23: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(22) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(22, value)
        }

    public var patrol24: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(23) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(23, value)
        }

    public var patrol25: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(24) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(24, value)
        }

    public var patrol26: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(25) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(25, value)
        }

    public var patrol27: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(26) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(26, value)
        }

    public var patrol28: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(27) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(27, value)
        }

    public var patrol29: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(28) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(28, value)
        }

    public var patrol30: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(29) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(29, value)
        }

    public var patrol31: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(30) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(30, value)
        }

    public var patrol32: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(31) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(31, value)
        }

    public var patrol33: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(32) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(32, value)
        }

    public var patrol34: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(33) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(33, value)
        }

    public var patrol35: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(34) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(34, value)
        }

    public var patrol36: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(35) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(35, value)
        }

    public var patrol37: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(36) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(36, value)
        }

    public var patrol38: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(37) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(37, value)
        }

    public var patrol39: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(38) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(38, value)
        }

    public var patrol40: NpcPatrolWaypoint
        get() = patrolList?.waypoints?.get(39) ?: throw IndexOutOfBoundsException()
        set(value) {
            patrolList = patrolList.insertAt(39, value)
        }

    public fun build(id: Int): UnpackedNpcType {
        backing.internal = internal
        if (param.isNotEmpty()) {
            backing.paramMap = param.toParamMap()
        }
        return backing.build(id)
    }

    public fun patrol(coords: CoordGrid, pauseDelay: Int): NpcPatrolWaypoint =
        NpcPatrolWaypoint(coords, pauseDelay)

    private fun NpcPatrol?.insertAt(index: Int, waypoint: NpcPatrolWaypoint): NpcPatrol =
        if (this == null) {
            NpcPatrol(listOf(waypoint))
        } else if (index == waypoints.size) {
            val newWaypoints = waypoints.toMutableList()
            newWaypoints.add(index, waypoint)
            NpcPatrol(newWaypoints)
        } else {
            val name = "patrol${waypoints.size + 1}"
            throw IndexOutOfBoundsException("You must fill out `$name` first.")
        }

    public val south: Direction = Direction.South
    public val north: Direction = Direction.North
    public val west: Direction = Direction.West
    public val east: Direction = Direction.East
    public val southwest: Direction = Direction.SouthWest
    public val northwest: Direction = Direction.NorthWest
    public val southeast: Direction = Direction.SouthEast
    public val northeast: Direction = Direction.NorthEast

    public val none: NpcMode = NpcMode.None
    public val wander: NpcMode = NpcMode.Wander
    public val patrol: NpcMode = NpcMode.Patrol

    public val blocked: MoveRestrict = MoveRestrict.Blocked
    public val blockednormal: MoveRestrict = MoveRestrict.BlockedNormal
    public val indoors: MoveRestrict = MoveRestrict.Indoors
    public val outdoors: MoveRestrict = MoveRestrict.Outdoors
    public val nomove: MoveRestrict = MoveRestrict.NoMove
    public val passthru: MoveRestrict = MoveRestrict.PassThru
}
