package org.runerealm.api.testing.scope

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provider
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import jakarta.inject.Inject
import kotlin.contracts.contract
import kotlin.reflect.KClass
import net.rsprot.protocol.game.incoming.buttons.If3Button
import net.rsprot.protocol.game.incoming.buttons.IfButtonD
import net.rsprot.protocol.game.incoming.buttons.IfButtonT
import net.rsprot.protocol.game.incoming.buttons.IfSubOp
import net.rsprot.protocol.game.incoming.locs.OpLoc
import net.rsprot.protocol.game.incoming.locs.OpLocT
import net.rsprot.protocol.game.incoming.misc.user.MoveGameClick
import net.rsprot.protocol.game.incoming.npcs.OpNpc
import net.rsprot.protocol.game.incoming.resumed.ResumePCountDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePStringDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePauseButton
import net.rsprot.protocol.game.outgoing.misc.player.MessageGame
import net.rsprot.protocol.util.CombinedId
import org.junit.jupiter.api.Assertions
import org.runerealm.annotations.InternalApi
import org.runerealm.api.account.character.CharacterDataStage
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.objs
import org.runerealm.api.db.Database
import org.runerealm.api.db.DatabaseConnection
import org.runerealm.api.drop.table.NpcDropTableProvider
import org.runerealm.api.drop.table.NpcDropTables
import org.runerealm.api.game.process.GameCycle
import org.runerealm.api.inv.map.InvMapInit
import org.runerealm.api.market.DefaultMarketPrices
import org.runerealm.api.market.MarketPrices
import org.runerealm.api.net.rsprot.handlers.If3ButtonHandler
import org.runerealm.api.net.rsprot.handlers.IfButtonDHandler
import org.runerealm.api.net.rsprot.handlers.IfButtonTHandler
import org.runerealm.api.net.rsprot.handlers.IfSubOpHandler
import org.runerealm.api.net.rsprot.handlers.MoveGameClickHandler
import org.runerealm.api.net.rsprot.handlers.OpLocHandler
import org.runerealm.api.net.rsprot.handlers.OpLocTHandler
import org.runerealm.api.net.rsprot.handlers.OpNpcHandler
import org.runerealm.api.net.rsprot.handlers.ResumePCountDialogHandler
import org.runerealm.api.net.rsprot.handlers.ResumePStringDialogHandler
import org.runerealm.api.net.rsprot.handlers.ResumePauseButtonHandler
import org.runerealm.api.npc.apPlayer2
import org.runerealm.api.npc.hit.modifier.NpcHitModifier
import org.runerealm.api.npc.hit.modifier.StandardNpcHitModifier
import org.runerealm.api.npc.hit.processor.NpcHitProcessor
import org.runerealm.api.npc.hit.processor.StandardNpcHitProcessor
import org.runerealm.api.npc.hit.queueHit
import org.runerealm.api.npc.interact.AiPlayerInteractions
import org.runerealm.api.npc.opPlayer2
import org.runerealm.api.player.hit.processor.DamageOnlyPlayerHitProcessor
import org.runerealm.api.player.hit.processor.InstantPlayerHitProcessor
import org.runerealm.api.player.interact.LocInteractions
import org.runerealm.api.player.interact.NpcInteractions
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.protect.clearPendingAction
import org.runerealm.api.player.ui.ifClose
import org.runerealm.api.player.ui.ifOpenMain
import org.runerealm.api.player.ui.ifOpenSub
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.vars.VarPlayerIntMapSetter
import org.runerealm.api.player.vars.varMoveSpeed
import org.runerealm.api.random.CoreRandom
import org.runerealm.api.random.DefaultGameRandom
import org.runerealm.api.random.GameRandom
import org.runerealm.api.realm.Realm
import org.runerealm.api.registry.account.AccountRegistry
import org.runerealm.api.registry.controller.ControllerRegistry
import org.runerealm.api.registry.controller.isSuccess
import org.runerealm.api.registry.loc.LocRegistry
import org.runerealm.api.registry.loc.LocRegistryNormal
import org.runerealm.api.registry.loc.LocRegistryRegion
import org.runerealm.api.registry.npc.NpcRegistry
import org.runerealm.api.registry.npc.isSuccess
import org.runerealm.api.registry.obj.ObjRegistry
import org.runerealm.api.registry.player.PlayerRegistry
import org.runerealm.api.registry.region.RegionRegistry
import org.runerealm.api.registry.zone.ZonePlayerActivityBitSet
import org.runerealm.api.repo.controller.ControllerRepository
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.repo.npc.NpcRepository
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.api.repo.player.PlayerRepository
import org.runerealm.api.repo.region.RegionRepository
import org.runerealm.api.repo.region.RegionTemplate
import org.runerealm.api.repo.world.WorldRepository
import org.runerealm.api.route.BoundValidator
import org.runerealm.api.route.RayCastFactory
import org.runerealm.api.route.RayCastValidator
import org.runerealm.api.route.RouteFactory
import org.runerealm.api.route.StepFactory
import org.runerealm.api.server.config.ServerConfigModule
import org.runerealm.api.stats.levelmod.InvisibleLevelMod
import org.runerealm.api.stats.levelmod.InvisibleLevels
import org.runerealm.api.stats.xpmod.XpMod
import org.runerealm.api.stats.xpmod.XpModifiers
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.capture.CaptureClient
import org.runerealm.api.testing.factory.collisionFactory
import org.runerealm.api.testing.random.FixedRandom
import org.runerealm.api.testing.random.SequenceRandom
import org.runerealm.api.testing.util.TestRealmConfig
import org.runerealm.api.utils.logging.GameExceptionHandler
import org.runerealm.events.EventBus
import org.runerealm.game.GameUpdate
import org.runerealm.game.MapClock
import org.runerealm.game.area.AreaIndex
import org.runerealm.game.cheat.CheatCommandMap
import org.runerealm.game.client.Client
import org.runerealm.game.dbtable.DbRowResolver
import org.runerealm.game.dbtable.DbTableResolver
import org.runerealm.game.entity.Controller
import org.runerealm.game.entity.ControllerList
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.NpcList
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.entity.player.SessionStateEvent
import org.runerealm.game.entity.util.PathingEntityCommon
import org.runerealm.game.entity.util.ShuffledPlayerList
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.game.hit.HitType
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.loc.LocAngle
import org.runerealm.game.loc.LocEntity
import org.runerealm.game.loc.LocInfo
import org.runerealm.game.loc.LocShape
import org.runerealm.game.loc.LocZoneKey
import org.runerealm.game.map.LocZoneStorage
import org.runerealm.game.map.collision.addLoc
import org.runerealm.game.movement.MoveSpeed
import org.runerealm.game.queue.EngineQueueCache
import org.runerealm.game.region.Region
import org.runerealm.game.region.RegionListLarge
import org.runerealm.game.region.RegionListSmall
import org.runerealm.game.stat.PlayerSkillXPTable
import org.runerealm.game.stat.PlayerStatMap
import org.runerealm.game.type.TypeListMap
import org.runerealm.game.type.area.AreaTypeList
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.comp.ComponentTypeList
import org.runerealm.game.type.content.ContentGroupType
import org.runerealm.game.type.controller.ControllerType
import org.runerealm.game.type.dbrow.DbRowTypeList
import org.runerealm.game.type.dbtable.DbTableTypeList
import org.runerealm.game.type.enums.EnumTypeList
import org.runerealm.game.type.font.FontMetricsTypeList
import org.runerealm.game.type.hunt.HuntModeTypeList
import org.runerealm.game.type.idk.IdentKitTypeList
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.type.interf.IfSubType
import org.runerealm.game.type.interf.InterfaceType
import org.runerealm.game.type.interf.InterfaceTypeList
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.loc.LocType
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.game.type.mod.ModLevelTypeList
import org.runerealm.game.type.npc.NpcTypeList
import org.runerealm.game.type.npc.UnpackedNpcType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.game.type.param.ParamTypeList
import org.runerealm.game.type.proj.ProjAnimTypeList
import org.runerealm.game.type.seq.SeqTypeList
import org.runerealm.game.type.stat.StatType
import org.runerealm.game.type.stat.StatTypeList
import org.runerealm.game.type.synth.SynthTypeList
import org.runerealm.game.type.varbit.VarBitType
import org.runerealm.game.type.varbit.VarBitTypeList
import org.runerealm.game.type.varn.VarnTypeList
import org.runerealm.game.type.varp.VarpType
import org.runerealm.game.type.varp.VarpTypeList
import org.runerealm.game.ui.UserInterface
import org.runerealm.map.CoordGrid
import org.runerealm.map.zone.ZoneGrid
import org.runerealm.map.zone.ZoneKey
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext
import org.runerealm.routefinder.collision.CollisionFlagMap
import org.runerealm.routefinder.loc.LocLayerConstants
import org.runerealm.server.app.modules.ParserModule

public class GameTestScope
@Inject
constructor(
    public val random: VariableGameRandom,
    public val mapClock: MapClock,
    public val eventBus: EventBus,
    public val players: PlayerList,
    public val objTypes: ObjTypeList,
    public val locTypes: LocTypeList,
    public val npcTypes: NpcTypeList,
    public val conRepo: ControllerRepository,
    public val npcRepo: NpcRepository,
    private val gameCycle: GameCycle,
    private val collision: CollisionFlagMap,
    private val locZoneStorage: LocZoneStorage,
    private val locRegistry: LocRegistry,
    private val locInteractions: LocInteractions,
    private val conRegistry: ControllerRegistry,
    private val npcRegistry: NpcRegistry,
    private val npcInteractions: NpcInteractions,
    private val interfaceTypes: InterfaceTypeList,
    private val invMapInit: InvMapInit,
    private val protectedAccess: ProtectedAccessLauncher,
    private val ifButtonHandler: If3ButtonHandler,
    private val ifButtonDHandler: IfButtonDHandler,
    private val ifButtonTHandler: IfButtonTHandler,
    private val ifSubOpHandler: IfSubOpHandler,
    private val gameClickHandler: MoveGameClickHandler,
    private val resumePCountDialog: ResumePCountDialogHandler,
    private val resumePStringDialog: ResumePStringDialogHandler,
    private val opLocHandler: OpLocHandler,
    private val opLocTHandler: OpLocTHandler,
    private val resumePauseButtonHandler: ResumePauseButtonHandler,
    private val opNpcHandler: OpNpcHandler,
    private val aiPlayerInteractions: AiPlayerInteractions,
    private val npcHitModifier: NpcHitModifier,
    private val regionRegistry: RegionRegistry,
    private val regionRepo: RegionRepository,
) {
    init {
        registerPlayer()
    }

    public val player: Player by lazy { players.first() }
    public val client: CaptureClient by lazy { player.captureClient }

    private val Player.captureClient
        get() = client as CaptureClient

    public val Player.stats: StatsDelegate
        get() = StatsDelegate(statMap)

    private var playerUuidCounter = 0L

    public fun advance(ticks: Int = 1) {
        repeat(ticks) {
            clearCaptureClients()
            gameCycle.tick()
            flushCaptureClients()
        }
    }

    public fun advanceUntil(
        predicate: () -> Boolean,
        timeoutTicks: Int = 1000,
        timeoutMessage: () -> String? = { null },
    ) {
        for (i in 0 until timeoutTicks) {
            if (predicate()) {
                return
            }
            advance()
        }
        val message = timeoutMessage() ?: "Could not advance after $timeoutTicks ticks!"
        throw IllegalStateException(message)
    }

    @OptIn(InternalApi::class)
    public fun registerPlayer(
        coords: CoordGrid = CoordGrid(0, 50, 50, 0, 0),
        player: Player = Player(),
        slot: Int = players.nextFreeSlot() ?: error("No available slot."),
        client: Client<Any, Any> = CaptureClient(),
        uuid: Long? = null,
    ): Player {
        val resolvedUuid = uuid ?: playerUuidCounter++
        player.coords = coords
        player.slotId = slot
        player.client = client
        player.uuid = resolvedUuid
        player.accountId = resolvedUuid.toInt()
        player.characterId = resolvedUuid.toInt()
        player.accountHash = resolvedUuid
        player.userId = resolvedUuid
        player.runEnergy = Int.MAX_VALUE
        player.assignUid()
        players[slot] = player
        eventBus.publish(SessionStateEvent.Initialize(player))
        if (player.invMap.isEmpty()) {
            invMapInit.init(player)
        }
        eventBus.publish(SessionStateEvent.Login(player))
        eventBus.publish(SessionStateEvent.EngineLogin(player))
        return player
    }

    @OptIn(InternalApi::class)
    public fun unregisterPlayer(player: Player) {
        require(player.slotId != PathingEntity.INVALID_SLOT) {
            "Invalid `slotId` for player: $player"
        }
        val slot = player.slotId
        player.slotId = -1
        player.clearUid()
        player.destroy()
        players.remove(slot)
    }

    public fun Player.setVarp(varp: VarpType, value: Int) {
        VarPlayerIntMapSetter.set(this, varp, value)
    }

    public fun Player.setVarBit(varbit: VarBitType, value: Int) {
        VarPlayerIntMapSetter.set(this, varbit, value)
    }

    public fun Player.setCurrentLevel(stat: StatType, level: Int) {
        stats.setCurrentLevel(stat, level)
    }

    public fun Player.setBaseLevel(stat: StatType, level: Int) {
        stats.setBaseLevel(stat, level)
    }

    public fun Player.setMoveSpeed(speed: MoveSpeed) {
        this.varMoveSpeed = speed
    }

    public fun Player.enableRun() {
        setMoveSpeed(MoveSpeed.Run)
    }

    public fun Player.enableWalk() {
        setMoveSpeed(MoveSpeed.Walk)
    }

    /**
     * Instantly sets the [Player]'s coordinates to [dest] without performing any of the updates or
     * side effects that normal teleport functions handle (such as updating the collision flag map).
     *
     * This function is intended for test setup only. It exists because using [teleport] or
     * [telejump] in tests would require calling [advance] to fully process the teleport logic and
     * any side effects, which can be unnecessary or cumbersome during test initialization.
     *
     * To simulate actual gameplay behavior during tests, use [teleport] or [telejump], as they
     * accurately reflect in-game mechanics and processing.
     */
    public fun Player.placeAt(dest: CoordGrid) {
        allocZoneCollision(dest)
        coords = dest
    }

    public fun Player.teleport(dest: CoordGrid) {
        allocZoneCollision(dest)
        PathingEntityCommon.teleport(this, collision, dest)
    }

    public fun Player.telejump(dest: CoordGrid) {
        allocZoneCollision(dest)
        PathingEntityCommon.telejump(this, collision, dest)
    }

    public fun Player.clearPendingAction() {
        clearPendingAction(eventBus)
    }

    public fun Player.clearInv(inv: Inventory = this.inv) {
        inv.fillNulls()
    }

    public fun Player.fillInv(with: InvObj = InvObj(objs.beer), inv: Inventory = this.inv) {
        repeat(inv.size) { inv[it] = InvObj(with) }
    }

    public fun Player.count(obj: ObjType, inv: Inventory = this.inv): Int = inv.count(obj)

    public fun Player.clearAllInvs() {
        invMap.values.forEach(Inventory::fillNulls)
    }

    public fun Player.opLoc1(loc: BoundLocInfo, controlKey: Boolean = false) {
        val message = OpLoc(loc.id, loc.x, loc.z, controlKey, op = 1)
        captureClient.queue(opLocHandler, message)
    }

    public fun Player.opLoc2(loc: BoundLocInfo, controlKey: Boolean = false) {
        val message = OpLoc(loc.id, loc.x, loc.z, controlKey, op = 2)
        captureClient.queue(opLocHandler, message)
    }

    public fun Player.opLoc3(loc: BoundLocInfo, controlKey: Boolean = false) {
        val message = OpLoc(loc.id, loc.x, loc.z, controlKey, op = 3)
        captureClient.queue(opLocHandler, message)
    }

    public fun Player.opLoc4(loc: BoundLocInfo, controlKey: Boolean = false) {
        val message = OpLoc(loc.id, loc.x, loc.z, controlKey, op = 4)
        captureClient.queue(opLocHandler, message)
    }

    public fun Player.opLoc5(loc: BoundLocInfo, controlKey: Boolean = false) {
        val message = OpLoc(loc.id, loc.x, loc.z, controlKey, op = 5)
        captureClient.queue(opLocHandler, message)
    }

    /**
     * Uses the inv obj in [invSlot] on [loc]. The inventory side panel is opened (with target
     * events enabled) if it is not already open.
     */
    public fun Player.opLocU(loc: BoundLocInfo, invSlot: Int, controlKey: Boolean = false) {
        val obj = checkNotNull(inv[invSlot]) { "No inv obj in slot `$invSlot`." }
        if (!ui.containsOverlay(interfaces.inventory)) {
            ifOpenOverlay(interfaces.inventory, components.toplevel_target_side3)
        }
        ifSetEvents(components.inv_items, inv.indices, IfEvent.TgtLoc)
        val selected = CombinedId(components.inv_items.interfaceId, components.inv_items.component)
        val message =
            OpLocT(
                id = loc.id,
                x = loc.coords.x,
                z = loc.coords.z,
                controlKey = controlKey,
                selectedObj = obj.id,
                selectedSub = invSlot,
                selectedCombinedId = selected,
            )
        captureClient.queue(opLocTHandler, message)
    }

    /**
     * Clicks held op [op] (`1`-`5`, e.g. `1` for "Eat") on the inventory obj in [invSlot], opening
     * the inventory overlay (with its op events enabled) if it is not already open.
     */
    public fun Player.opHeld(invSlot: Int, op: Int = 1) {
        val obj = checkNotNull(inv[invSlot]) { "No inv obj in slot `$invSlot`." }
        if (!ui.containsOverlay(interfaces.inventory)) {
            ifOpenOverlay(interfaces.inventory, components.toplevel_target_side3)
        }
        ifSetEvents(
            components.inv_items,
            inv.indices,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op6,
            IfEvent.Op7,
        )
        // The inventory's interface ops are offset from the obj's held ops.
        val buttonOp =
            when (op) {
                1 -> IfButtonOp.Op2
                2 -> IfButtonOp.Op3
                3 -> IfButtonOp.Op4
                4 -> IfButtonOp.Op6
                5 -> IfButtonOp.Op7
                else -> throw IllegalArgumentException("Invalid held op: $op")
            }
        ifButton(components.inv_items, comsub = invSlot, op = buttonOp, obj = obj.id)
    }

    /**
     * Picks sub-op [subop] (1-based) from held op [op]'s right-click submenu on the inventory obj
     * in [invSlot] (the client's `IF_SUBOP`), e.g. "Rub" → "Edgeville" on an amulet of glory.
     */
    public fun Player.opHeldSubOp(invSlot: Int, op: Int, subop: Int) {
        val obj = checkNotNull(inv[invSlot]) { "No inv obj in slot `$invSlot`." }
        if (!ui.containsOverlay(interfaces.inventory)) {
            ifOpenOverlay(interfaces.inventory, components.toplevel_target_side3)
        }
        ifSetEvents(
            components.inv_items,
            inv.indices,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op6,
            IfEvent.Op7,
        )
        val buttonOp =
            when (op) {
                1 -> IfButtonOp.Op2
                2 -> IfButtonOp.Op3
                3 -> IfButtonOp.Op4
                4 -> IfButtonOp.Op6
                5 -> IfButtonOp.Op7
                else -> throw IllegalArgumentException("Invalid held op: $op")
            }
        val combinedId =
            CombinedId(components.inv_items.interfaceId, components.inv_items.component)
        val message = IfSubOp(combinedId, invSlot, obj.id, buttonOp.slot, subop)
        captureClient.queue(ifSubOpHandler, message)
    }

    /** Uses the inventory obj in [selectedSlot] on the inventory obj in [targetSlot]. */
    public fun Player.opHeldU(selectedSlot: Int, targetSlot: Int) {
        val selected = checkNotNull(inv[selectedSlot]) { "No inv obj in slot `$selectedSlot`." }
        val target = checkNotNull(inv[targetSlot]) { "No inv obj in slot `$targetSlot`." }
        if (!ui.containsOverlay(interfaces.inventory)) {
            ifOpenOverlay(interfaces.inventory, components.toplevel_target_side3)
        }
        ifSetEvents(components.inv_items, inv.indices, IfEvent.TgtCom, IfEvent.Target)
        val combinedId =
            CombinedId(components.inv_items.interfaceId, components.inv_items.component)
        val message =
            IfButtonT(combinedId, selectedSlot, selected.id, combinedId, targetSlot, target.id)
        captureClient.queue(ifButtonTHandler, message)
    }

    public fun Player.resumePauseButton(component: ComponentType, sub: Int = -1) {
        val combinedId = CombinedId(component.interfaceId, component.component)
        val message = ResumePauseButton(combinedId, sub)
        captureClient.queue(resumePauseButtonHandler, message)
    }

    public fun Player.opNpc1(npc: Npc, controlKey: Boolean = false) {
        val message = OpNpc(npc.slotId, controlKey, op = 1)
        captureClient.queue(opNpcHandler, message)
    }

    public fun Player.opNpc2(npc: Npc, controlKey: Boolean = false) {
        val message = OpNpc(npc.slotId, controlKey, op = 2)
        captureClient.queue(opNpcHandler, message)
    }

    public fun Player.opNpc3(npc: Npc, controlKey: Boolean = false) {
        val message = OpNpc(npc.slotId, controlKey, op = 3)
        captureClient.queue(opNpcHandler, message)
    }

    public fun Player.opNpc4(npc: Npc, controlKey: Boolean = false) {
        val message = OpNpc(npc.slotId, controlKey, op = 4)
        captureClient.queue(opNpcHandler, message)
    }

    public fun Player.opNpc5(npc: Npc, controlKey: Boolean = false) {
        val message = OpNpc(npc.slotId, controlKey, op = 5)
        captureClient.queue(opNpcHandler, message)
    }

    public fun Player.ifOpenMain(interf: InterfaceType) {
        ifOpenMain(interf, eventBus)
    }

    public fun Player.ifOpenOverlay(interf: InterfaceType, target: ComponentType) {
        ifOpenSub(interf, target, IfSubType.Overlay, eventBus)
    }

    public fun Player.ifClose() {
        ifClose(eventBus)
    }

    public fun Player.ifButton(
        type: ComponentType,
        comsub: Int? = null,
        op: IfButtonOp = IfButtonOp.Op1,
        obj: Int? = null,
    ) {
        val combinedId = CombinedId(type.interfaceId, type.component)
        val message = If3Button(combinedId, comsub ?: -1, obj = obj ?: -1, op = op.slot)
        captureClient.queue(ifButtonHandler, message)
    }

    public fun Player.ifButtonD(
        fromComponent: ComponentType,
        fromComsub: Int,
        fromObj: ObjType?,
        intoComponent: ComponentType,
        intoComsub: Int,
        intoObj: ObjType?,
    ) {
        val fromCombinedId = CombinedId(fromComponent.interfaceId, fromComponent.component)
        val intoCombinedId = CombinedId(intoComponent.interfaceId, intoComponent.component)
        val message =
            IfButtonD(
                fromCombinedId,
                fromComsub,
                fromObj?.id ?: -1,
                intoCombinedId,
                intoComsub,
                intoObj?.id ?: -1,
            )
        captureClient.queue(ifButtonDHandler, message)
    }

    public fun Player.ifButtonD(
        fromComponent: ComponentType,
        fromComsub: Int,
        intoComsub: Int,
        fromObj: ObjType? = null,
        intoObj: ObjType? = null,
    ) {
        ifButtonD(
            fromComponent = fromComponent,
            fromComsub = fromComsub,
            fromObj = fromObj,
            intoComponent = fromComponent,
            intoComsub = intoComsub,
            intoObj = intoObj,
        )
    }

    public fun Player.resumeCountDialog(count: Int) {
        val message = ResumePCountDialog(count)
        captureClient.queue(resumePCountDialog, message)
    }

    public fun Player.resumeStringDialog(text: String) {
        val message = ResumePStringDialog(text)
        captureClient.queue(resumePStringDialog, message)
    }

    public fun Player.moveGameClick(dest: CoordGrid, keyCombination: Int = 0) {
        allocZoneCollision(dest)
        val message = MoveGameClick(dest.x, dest.z, keyCombination)
        captureClient.queue(gameClickHandler, message)
    }

    public fun Player.withProtectedAccess(action: suspend ProtectedAccess.() -> Unit) {
        protectedAccess.launch(this) { action() }
    }

    public fun Npc.telejump(dest: CoordGrid) {
        allocZoneCollision(dest)
        PathingEntityCommon.telejump(this, collision, dest)
    }

    public fun Npc.opPlayer2(target: Player) {
        opPlayer2(target, aiPlayerInteractions)
    }

    public fun Npc.apPlayer2(target: Player) {
        apPlayer2(target, aiPlayerInteractions)
    }

    public fun Npc.queueHit(source: Player, delay: Int, type: HitType, damage: Int) {
        queueHit(source, delay, type, damage, npcHitModifier)
    }

    public fun Inventory.count(obj: ObjType): Int {
        return count(objTypes[obj])
    }

    public fun allocZoneCollision(coord: CoordGrid) {
        collision.allocateIfAbsent(coord.x, coord.z, coord.level)
    }

    public fun spawnNpc(coords: CoordGrid, type: UnpackedNpcType, init: Npc.() -> Unit = {}): Npc {
        val npc = Npc(type, coords).apply(init)
        val add = npcRegistry.add(npc)
        check(add.isSuccess()) { "Could not add npc: result=$add, npc=$npc" }
        return npc
    }

    public fun spawnController(
        coords: CoordGrid,
        type: ControllerType,
        duration: Int = Int.MAX_VALUE,
        init: Controller.() -> Unit = {},
    ): Controller {
        val controller = Controller(type, coords).apply { duration(duration) }.apply(init)
        val add = conRegistry.add(controller)
        check(add.isSuccess()) { "Could not add controller: result=$add, controller=$controller" }
        return controller
    }

    public fun placeMapLoc(
        coords: CoordGrid,
        type: UnpackedLocType,
        shape: LocShape = LocShape.CentrepieceStraight,
        angle: LocAngle = LocAngle.West,
    ): BoundLocInfo {
        val entity = LocEntity(type.id, shape.id, angle.id)
        val locInfo = LocInfo(LocLayerConstants.of(shape.id), coords, entity)
        val boundLoc = BoundLocInfo(locInfo, type)
        collision.addLoc(locInfo, type)

        val zoneKey = ZoneKey.from(coords)
        val zoneGrid = ZoneGrid.from(coords)
        val locZoneKey = LocZoneKey(zoneGrid, locInfo.layer)
        locZoneStorage.mapLocs[zoneKey, locZoneKey] = entity

        // Since packet handlers check for valid interactions with `locInfo`, we need to register
        // the [UnpackedLocType] in the test-injected [LocTypeList].
        // Without this, [LocInteractions.hasOp] will return `false`, blocking the interaction
        // at the packet-handler level.
        // Note that this mutation affects only _this test scope's_ [LocTypeList] and does
        // not alter any other instance.
        locTypes.types[type.id] = type

        return boundLoc
    }

    public fun placeMapLoc(
        coords: CoordGrid,
        type: LocType,
        shape: LocShape = LocShape.CentrepieceStraight,
        angle: LocAngle = LocAngle.West,
    ): BoundLocInfo = placeMapLoc(coords, locTypes[type], shape, angle)

    public fun locDel(bound: BoundLocInfo) {
        val locInfo = LocInfo(bound.layer, bound.coords, bound.entity)
        locDel(locInfo)
    }

    public fun locDel(loc: LocInfo) {
        locRegistry.del(loc)
    }

    public fun findLocs(coords: CoordGrid): Sequence<LocInfo> =
        locRegistry.findAll(ZoneKey.from(coords))

    public fun findLoc(coords: CoordGrid, type: LocType): LocInfo? =
        findLocs(coords).firstOrNull { it.id == type.id }

    public fun locExists(loc: BoundLocInfo): Boolean = locRegistry.isValid(loc.coords, loc.id)

    public fun locExists(coords: CoordGrid, type: LocType): Boolean =
        locRegistry.isValid(coords, type.id)

    public fun findLocTypes(predicate: (UnpackedLocType) -> Boolean): Sequence<UnpackedLocType> {
        return sequence {
            for (type in locTypes.values) {
                if (predicate(type)) {
                    yield(type)
                }
            }
        }
    }

    public fun findLocType(
        content: ContentGroupType,
        predicate: (UnpackedLocType) -> Boolean = { true },
    ): UnpackedLocType = findLocTypes { it.isContentType(content) && predicate(it) }.first()

    public fun findObjType(predicate: (UnpackedObjType) -> Boolean): Sequence<UnpackedObjType> {
        return sequence {
            for (type in objTypes.values) {
                if (predicate(type)) {
                    yield(type)
                }
            }
        }
    }

    public fun firstObjType(predicate: (UnpackedObjType) -> Boolean): UnpackedObjType {
        val filtered = findObjType(predicate)
        return filtered.firstOrNull()
            ?: throw NoSuchElementException("No ObjType found with given predicate.")
    }

    public fun createRegionOrNull(template: RegionTemplate): Region? {
        return regionRepo.add(template)
    }

    public fun createRegion(template: RegionTemplate): Region {
        return createRegionOrNull(template) ?: error("Could not create region.")
    }

    public fun removeInactiveRegions() {
        regionRegistry.removeInactiveSmallRegions()
        regionRegistry.removeInactiveLargeRegions()
    }

    public fun CaptureClient.clear() {
        clearOutgoing()
    }

    private fun clearCaptureClients() {
        for (player in players) {
            val client = player.captureClient
            client.clearOutgoing()
        }
    }

    private fun flushCaptureClients() {
        for (player in players) {
            val client = player.captureClient
            client.clearIncoming()
        }
    }

    public fun assertTrue(condition: Boolean) {
        contract { returns() implies condition }
        Assertions.assertTrue(condition)
    }

    public fun assertTrue(condition: Boolean, message: () -> String) {
        contract { returns() implies condition }
        Assertions.assertTrue(condition, message)
    }

    public fun assertFalse(condition: Boolean) {
        contract { returns() implies !condition }
        Assertions.assertFalse(condition)
    }

    public fun assertNull(actual: Any?) {
        contract { returns() implies (actual == null) }
        Assertions.assertNull(actual)
    }

    public fun assertNotNull(actual: Any?) {
        contract { returns() implies (actual != null) }
        Assertions.assertNotNull(actual)
    }

    public fun assertEquals(expected: Any?, actual: Any?) {
        Assertions.assertEquals(expected, actual)
    }

    public fun assertNotEquals(expected: Any?, actual: Any?) {
        Assertions.assertNotEquals(expected, actual)
    }

    public fun assertContains(inv: Inventory, obj: ObjType) {
        Assertions.assertTrue(obj in inv) { "Obj not found. (obj=$obj) | (inv=$inv)" }
    }

    public fun assertDoesNotContain(inv: Inventory, obj: ObjType) {
        Assertions.assertFalse(obj in inv) { "Obj found. (obj=$obj) | (inv=$inv)" }
    }

    public fun assertExists(loc: BoundLocInfo) {
        Assertions.assertTrue(locExists(loc)) {
            val found = locRegistry.findAll(ZoneKey.from(loc.coords)).toList()
            "Loc not found. (loc=$loc) | (found=$found)"
        }
    }

    public fun assertExists(coords: CoordGrid, type: LocType) {
        Assertions.assertTrue(locExists(coords, type)) {
            val found = locRegistry.findAll(ZoneKey.from(coords)).toList()
            "Loc not found. (coords=$coords, type=$type) | (found=$found)"
        }
    }

    public fun assertDoesNotExist(loc: BoundLocInfo) {
        Assertions.assertFalse(locExists(loc)) { "Loc found. (loc=$loc)" }
    }

    public fun assertDoesNotExist(coords: CoordGrid, type: LocType) {
        Assertions.assertFalse(locExists(coords, type)) {
            "Loc found. (coords=$coords) | (type=$type)"
        }
    }

    public fun assertMessageSent(text: String, player: Player = this.player) {
        val messages = player.captureClient.mapOf(MessageGame::message)
        val sent = messages.contains(text)
        Assertions.assertTrue(sent) { "Message not found. (search=$text) | (messages=$messages)" }
    }

    public fun assertMessageNotSent(text: String, player: Player = this.player) {
        val messages = player.captureClient.mapOf(MessageGame::message)
        val sent = messages.contains(text)
        Assertions.assertFalse(sent) { "Message found. (search=$text) | (messages=$messages)" }
    }

    public fun assertMessagesSent(vararg text: String, player: Player = this.player) {
        require(text.isNotEmpty()) { "Must provide at least one `text` argument." }
        val messages = player.captureClient.mapOf(MessageGame::message)
        val notFound = text.filter { search -> messages.none { search == it } }
        Assertions.assertTrue(notFound.isEmpty()) {
            "Messages not found. (notFound=$notFound) | (messages=$messages)"
        }
    }

    public fun assertNoMessageSent(player: Player = this.player) {
        val messages = player.captureClient.mapOf(MessageGame::message)
        Assertions.assertEquals(emptyList<String>(), messages) { "Messages found:" }
    }

    public fun assertModalOpen(interf: InterfaceType, player: Player = this.player) {
        Assertions.assertTrue(player.ui.containsModal(interf)) {
            val openedModals = player.ui.modals.values.map(::UserInterface)
            "Modal not opened. (expected=$interf) | (found=$openedModals) | (player=$player)"
        }
    }

    public fun assertModalNotOpen(interf: InterfaceType, player: Player = this.player) {
        Assertions.assertFalse(player.ui.containsModal(interf)) {
            val openedModals = player.ui.modals.values.map(::UserInterface)
            "Modal is opened. (modal=$interf) | (opened=$openedModals) | (player=$player)"
        }
    }

    /**
     * This requires a context (`Player` in this case) so that after catching the expected
     * [Throwable] the entity can be registered into the game world once again if they were
     * disconnected.
     */
    public inline fun <reified T : Throwable> Player.assertThrows(block: () -> Unit): T {
        val startClient = client
        val startSlot = slotId
        val startUuid = uuid
        val caught: Throwable? =
            try {
                client = CaptureClient()
                block()
                null
            } catch (t: Throwable) {
                t
            }

        Assertions.assertNotNull(caught) {
            "Expected ${T::class.java.simpleName} to be thrown, but nothing was thrown."
        }

        Assertions.assertInstanceOf(T::class.java, caught) {
            "Expected ${T::class.java.simpleName} to be thrown, " +
                "but ${caught?.javaClass?.simpleName} was thrown."
        }

        // We want to restore the player's capture client before any errors occurred.
        this.client = startClient

        // If player was disconnected due to the error, we should re-register them.
        if (slotId == PathingEntity.INVALID_SLOT && startSlot != PathingEntity.INVALID_SLOT) {
            registerPlayer(coords, this, startSlot, startClient, startUuid)
        }
        return caught as T
    }

    public fun assertDoesNotThrow(msg: String, block: () -> Unit) {
        Assertions.assertDoesNotThrow({ block() }, msg)
    }

    public fun assertDoesNotThrow(block: () -> Unit) {
        Assertions.assertDoesNotThrow { block() }
    }

    public class Builder(state: GameTestState, private val scripts: Set<KClass<out PluginScript>>) {
        private val cacheTypes: TypeListMap = state.cacheTypes
        private val collisionMap: CollisionFlagMap = state.collision

        internal fun build(): GameTestScope {
            val module = TestModule(cacheTypes, collisionMap)
            val injector = Guice.createInjector(module)
            bindScriptEvents(injector)
            return injector.getInstance(GameTestScope::class.java)
        }

        internal fun buildInjector(optionalChildModule: AbstractModule?): Injector {
            val module = TestModule(cacheTypes, collisionMap)
            val parentInjector = Guice.createInjector(module)
            val injector =
                if (optionalChildModule != null) {
                    parentInjector.createChildInjector(optionalChildModule)
                } else {
                    parentInjector
                }
            bindScriptEvents(injector)
            return injector
        }

        private fun bindScriptEvents(injector: Injector) {
            val context = injector.getInstance(ScriptContext::class.java)
            for (clazz in scripts) {
                val script = injector.getInstance(clazz.java)
                with(script) { context.startup() }
            }
        }
    }

    private class TestModule(
        private val cacheTypes: TypeListMap,
        private val gameCollisionMap: CollisionFlagMap,
    ) : AbstractModule() {
        override fun configure() {
            bindInstances()
            installModules()
        }

        private fun bindInstances() {
            collisionFactory.borrowSharedMap().let { collision ->
                // Copy the original game's collision flag map into the test.
                // Important Note: This does _not_ add locs into the loc registry.
                gameCollisionMap.flags.copyInto(collision.flags)
                bind(CollisionFlagMap::class.java).toInstance(collision)
            }

            bind(AreaIndex::class.java).`in`(Scopes.SINGLETON)

            bind(GameExceptionHandler::class.java)
                .to(TestExceptionHandler::class.java)
                .`in`(Scopes.SINGLETON)

            bind(GameRandom::class.java)
                .annotatedWith(CoreRandom::class.java)
                .toInstance(FixedRandom(start = 0))

            VariableGameRandom().let { random ->
                bind(GameRandom::class.java).toInstance(random.impl)
                bind(VariableGameRandom::class.java).toInstance(random)
            }

            bind(Realm::class.java).toInstance(createTestRealm())
            bind(Database::class.java).to(ThrowDatabase::class.java).`in`(Scopes.SINGLETON)

            bind(EventBus::class.java).`in`(Scopes.SINGLETON)
            bind(GameUpdate::class.java).`in`(Scopes.SINGLETON)
            bind(MapClock::class.java).`in`(Scopes.SINGLETON)
            bind(LocZoneStorage::class.java).`in`(Scopes.SINGLETON)

            bind(BoundValidator::class.java).`in`(Scopes.SINGLETON)
            bind(RayCastValidator::class.java).`in`(Scopes.SINGLETON)
            bind(RayCastFactory::class.java).`in`(Scopes.SINGLETON)
            bind(RouteFactory::class.java).`in`(Scopes.SINGLETON)
            bind(StepFactory::class.java).`in`(Scopes.SINGLETON)

            bind(AccountRegistry::class.java).`in`(Scopes.SINGLETON)
            bind(ControllerList::class.java).`in`(Scopes.SINGLETON)
            bind(ControllerRegistry::class.java).`in`(Scopes.SINGLETON)
            bind(ControllerRepository::class.java).`in`(Scopes.SINGLETON)
            bind(PlayerList::class.java).`in`(Scopes.SINGLETON)
            bind(PlayerRegistry::class.java).`in`(Scopes.SINGLETON)
            bind(PlayerRepository::class.java).`in`(Scopes.SINGLETON)
            bind(ZonePlayerActivityBitSet::class.java).`in`(Scopes.SINGLETON)
            bind(NpcList::class.java).`in`(Scopes.SINGLETON)
            bind(NpcRegistry::class.java).`in`(Scopes.SINGLETON)
            bind(NpcRepository::class.java).`in`(Scopes.SINGLETON)
            bind(LocRegistry::class.java).`in`(Scopes.SINGLETON)
            bind(LocRegistryNormal::class.java).`in`(Scopes.SINGLETON)
            bind(LocRegistryRegion::class.java).`in`(Scopes.SINGLETON)
            bind(LocRepository::class.java).`in`(Scopes.SINGLETON)
            bind(RegionListSmall::class.java).`in`(Scopes.SINGLETON)
            bind(RegionListLarge::class.java).`in`(Scopes.SINGLETON)
            bind(RegionRegistry::class.java).`in`(Scopes.SINGLETON)
            bind(RegionRepository::class.java).`in`(Scopes.SINGLETON)
            bind(ObjRegistry::class.java).`in`(Scopes.SINGLETON)
            bind(ObjRepository::class.java).`in`(Scopes.SINGLETON)
            bind(WorldRepository::class.java).`in`(Scopes.SINGLETON)

            // These type lists can be modified by tests to ensure interactions pass
            // validation checks. Tests need the flexibility to create and modify these
            // types. Without this, interactions with custom "test types" would likely
            // fail at the packet-handler level, as the associated `UnpackedXType.op`
            // is checked for validity.
            //
            // The word "likely" is used because the id of a test type may conflict with
            // an existing type that has the required op, causing the interaction to pass
            // the validity check by coincidence.
            bind(LocTypeList::class.java).toInstance(cacheTypes.locs.copy())
            bind(ObjTypeList::class.java).toInstance(cacheTypes.objs.copy())

            // Though similar, the npc type list **does not** require this flexibility.
            // Each `Npc` stores a reference to its `UnpackedNpcType` upon creation,
            // ensuring that any test type used to spawn a npc will automatically pass
            // the op validity check. It is safe to share the same `NpcTypeList`.
            bind(NpcTypeList::class.java).toInstance(cacheTypes.npcs)

            // Currently, there is no need to mutate the following type lists.
            // Therefore, we can reuse their shared instance across test scopes.
            bind(AreaTypeList::class.java).toInstance(cacheTypes.areas)
            bind(ComponentTypeList::class.java).toInstance(cacheTypes.components)
            bind(DbRowTypeList::class.java).toInstance(cacheTypes.dbRows)
            bind(DbTableTypeList::class.java).toInstance(cacheTypes.dbTables)
            bind(EnumTypeList::class.java).toInstance(cacheTypes.enums)
            bind(HuntModeTypeList::class.java).toInstance(cacheTypes.hunt)
            bind(IdentKitTypeList::class.java).toInstance(cacheTypes.identKits)
            bind(FontMetricsTypeList::class.java).toInstance(cacheTypes.fonts)
            bind(InterfaceTypeList::class.java).toInstance(cacheTypes.interfaces)
            bind(InvTypeList::class.java).toInstance(cacheTypes.invs)
            bind(ModLevelTypeList::class.java).toInstance(cacheTypes.modLevels)
            bind(ProjAnimTypeList::class.java).toInstance(cacheTypes.projanims)
            bind(ParamTypeList::class.java).toInstance(cacheTypes.params)
            bind(SeqTypeList::class.java).toInstance(cacheTypes.seqs)
            bind(StatTypeList::class.java).toInstance(cacheTypes.stats)
            bind(SynthTypeList::class.java).toInstance(cacheTypes.synths)
            bind(VarBitTypeList::class.java).toInstance(cacheTypes.varbits)
            bind(VarpTypeList::class.java).toInstance(cacheTypes.varps)
            bind(VarnTypeList::class.java).toInstance(cacheTypes.varns)

            bind(TypeListMap::class.java).toInstance(cacheTypes)

            bind(CheatCommandMap::class.java).`in`(Scopes.SINGLETON)
            bind(EngineQueueCache::class.java).`in`(Scopes.SINGLETON)

            Multibinder.newSetBinder(binder(), NpcDropTableProvider::class.java)
            bind(NpcDropTables::class.java).`in`(Scopes.SINGLETON)

            Multibinder.newSetBinder(binder(), InvisibleLevelMod::class.java)
            bind(InvisibleLevels::class.java).`in`(Scopes.SINGLETON)

            Multibinder.newSetBinder(binder(), XpMod::class.java)
            bind(XpModifiers::class.java).`in`(Scopes.SINGLETON)

            bind(MarketPrices::class.java)
                .to(DefaultMarketPrices::class.java)
                .`in`(Scopes.SINGLETON)

            bind(DbRowResolver::class.java)
                .toProvider(DbRowResolverProvider::class.java)
                .`in`(Scopes.SINGLETON)

            bind(DbTableResolver::class.java)
                .toProvider(DbTableResolverProvider::class.java)
                .`in`(Scopes.SINGLETON)

            bind(EnumTypeMapResolver::class.java)
                .toProvider(EnumTypeMapResolverProvider::class.java)
                .`in`(Scopes.SINGLETON)

            bind(ShuffledPlayerList::class.java)
                .toProvider(ShuffledPlayerListProvider::class.java)
                .`in`(Scopes.SINGLETON)

            bind(NpcHitModifier::class.java).to(StandardNpcHitModifier::class.java)
            bind(NpcHitProcessor::class.java).to(StandardNpcHitProcessor::class.java)
            bind(InstantPlayerHitProcessor::class.java).to(DamageOnlyPlayerHitProcessor::class.java)

            Multibinder.newSetBinder(binder(), CharacterDataStage.Pipeline::class.java)
        }

        private fun installModules() {
            install(ParserModule)
            install(ServerConfigModule)
        }

        private fun createTestRealm(): Realm {
            val realm = Realm(name = "test-suite")
            realm.updateConfig(TestRealmConfig.create())
            return realm
        }

        private class DbRowResolverProvider @Inject constructor(private val types: TypeListMap) :
            Provider<DbRowResolver> {
            override fun get(): DbRowResolver = DbRowResolver(types)
        }

        private class DbTableResolverProvider @Inject constructor(private val types: TypeListMap) :
            Provider<DbTableResolver> {
            override fun get(): DbTableResolver = DbTableResolver(types)
        }

        private class EnumTypeMapResolverProvider
        @Inject
        constructor(private val enums: EnumTypeList) : Provider<EnumTypeMapResolver> {
            override fun get(): EnumTypeMapResolver = EnumTypeMapResolver(enums)
        }

        private class ShuffledPlayerListProvider
        @Inject
        constructor(private val playerList: PlayerList) : Provider<ShuffledPlayerList> {
            override fun get(): ShuffledPlayerList = ShuffledPlayerList(playerList)
        }

        private class ThrowDatabase : Database {
            override suspend fun <T> withTransaction(block: (DatabaseConnection) -> T): T =
                error(
                    "ThrowDatabase was used: no real Database is available. " +
                        "If your test needs database access, bind a test-specific Database."
                )
        }
    }

    @Suppress("konsist.avoid usage of stdlib Random in properties")
    public class VariableGameRandom {
        internal val impl: InternalRandomImpl = InternalRandomImpl()
        private val sequence = SequenceRandom(size = 128)
        private val default = DefaultGameRandom(seed = 123456)

        /** @see [SequenceRandom.next] */
        public var next: Int by sequence::next

        /** @see [SequenceRandom.then] */
        public var then: Int by sequence::then

        public var nextBoolean: Boolean
            get() = if (sequence.hasNext) sequence.next == 0 else default.randomBoolean()
            set(value) {
                next = if (value) 0 else 1
            }

        internal inner class InternalRandomImpl : GameRandom {
            override fun of(maxExclusive: Int): Int {
                return if (sequence.hasNext) {
                    sequence.of(maxExclusive)
                } else {
                    default.of(maxExclusive)
                }
            }

            override fun of(minInclusive: Int, maxInclusive: Int): Int {
                return if (sequence.hasNext) {
                    sequence.of(minInclusive, maxInclusive)
                } else {
                    default.of(minInclusive, maxInclusive)
                }
            }

            override fun randomDouble(): Double {
                return if (sequence.hasNext) {
                    sequence.randomDouble()
                } else {
                    default.randomDouble()
                }
            }
        }
    }

    public class StatsDelegate(private val backing: PlayerStatMap) {
        @OptIn(InternalApi::class)
        public operator fun get(stat: StatType): Int =
            backing.getCurrentLevel(stat).toInt() and 0xFF

        public operator fun set(stat: StatType, value: Int) {
            backing.setBaseLevel(stat, value.toByte())
            backing.setCurrentLevel(stat, value.toByte())
            if (value <= 0) {
                backing.setFineXP(stat, 0)
            } else {
                val xp = PlayerSkillXPTable.getFineXPFromLevel(value)
                backing.setFineXP(stat, xp)
            }
        }

        public fun setBaseLevel(stat: StatType, value: Int) {
            backing.setBaseLevel(stat, value.toByte())
            if (value <= 0) {
                backing.setFineXP(stat, 0)
            } else {
                val xp = PlayerSkillXPTable.getFineXPFromLevel(value)
                backing.setFineXP(stat, xp)
            }
        }

        public fun setCurrentLevel(stat: StatType, value: Int) {
            backing.setCurrentLevel(stat, value.toByte())
        }
    }

    private class TestExceptionHandler : GameExceptionHandler {
        override fun handle(t: Throwable, msg: () -> Any?) {
            throw t
        }
    }
}
