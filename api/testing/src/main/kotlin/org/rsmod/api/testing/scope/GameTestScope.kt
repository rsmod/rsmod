package org.rsmod.api.testing.scope

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
import net.rsprot.protocol.game.incoming.misc.user.MoveGameClick
import net.rsprot.protocol.game.incoming.resumed.ResumePCountDialog
import net.rsprot.protocol.game.outgoing.misc.player.MessageGame
import net.rsprot.protocol.util.CombinedId
import org.junit.jupiter.api.Assertions
import org.rsmod.api.config.refs.objs
import org.rsmod.api.game.process.GameCycle
import org.rsmod.api.inv.map.InvMapInit
import org.rsmod.api.market.DefaultMarketPrices
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.net.rsprot.handlers.If3ButtonHandler
import org.rsmod.api.net.rsprot.handlers.IfButtonDHandler
import org.rsmod.api.net.rsprot.handlers.MoveGameClickHandler
import org.rsmod.api.net.rsprot.handlers.ResumePCountDialogHandler
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifOpenMain
import org.rsmod.api.player.ui.ifOpenSub
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.api.random.CoreRandom
import org.rsmod.api.random.DefaultGameRandom
import org.rsmod.api.random.GameRandom
import org.rsmod.api.registry.controller.ControllerRegistry
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.loc.LocRegistryNormal
import org.rsmod.api.registry.loc.LocRegistryRegion
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.registry.npc.isSuccess
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.api.registry.zone.ZonePlayerActivityBitSet
import org.rsmod.api.repo.controller.ControllerRepository
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.repo.player.PlayerRepository
import org.rsmod.api.repo.region.RegionRepository
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.api.route.BoundValidator
import org.rsmod.api.route.RayCastFactory
import org.rsmod.api.route.RayCastValidator
import org.rsmod.api.route.RouteFactory
import org.rsmod.api.route.StepFactory
import org.rsmod.api.stats.levelmod.InvisibleLevelMod
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.api.stats.xpmod.XpMod
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.capture.CaptureClient
import org.rsmod.api.testing.factory.collisionFactory
import org.rsmod.api.testing.random.SequenceRandom
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.cheat.CheatCommandMap
import org.rsmod.game.client.Client
import org.rsmod.game.entity.ControllerList
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.player.SessionStateEvent
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.inv.Inventory
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocShape
import org.rsmod.game.loc.LocZoneKey
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.game.map.collision.addLoc
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.obj.InvObj
import org.rsmod.game.region.RegionListLarge
import org.rsmod.game.region.RegionListSmall
import org.rsmod.game.stat.PlayerSkillXPTable
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.font.FontMetricsTypeList
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamTypeList
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.game.type.synth.SynthTypeList
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.ui.UserInterface
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.loc.LocLayerConstants

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
    private val gameCycle: GameCycle,
    private val collision: CollisionFlagMap,
    private val locZoneStorage: LocZoneStorage,
    private val locRegistry: LocRegistry,
    private val locInteractions: LocInteractions,
    private val npcRegistry: NpcRegistry,
    private val npcInteractions: NpcInteractions,
    private val interfaceTypes: InterfaceTypeList,
    private val invMapInit: InvMapInit,
    private val protectedAccess: ProtectedAccessLauncher,
    private val ifButtonHandler: If3ButtonHandler,
    private val ifButtonDHandler: IfButtonDHandler,
    private val gameClickHandler: MoveGameClickHandler,
    private val resumePCountDialog: ResumePCountDialogHandler,
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

    public fun registerPlayer(
        coords: CoordGrid = CoordGrid(0, 50, 50, 0, 0),
        player: Player = Player(),
        slot: Int = players.nextFreeSlot() ?: error("No available slot."),
        client: Client<Any, Any> = CaptureClient(),
    ): Player {
        player.coords = coords
        player.slotId = slot
        player.client = client
        players[slot] = player
        eventBus.publish(SessionStateEvent.Initialize(player))
        if (player.invMap.isEmpty()) {
            invMapInit.init(player)
        }
        eventBus.publish(SessionStateEvent.LogIn(player))
        return player
    }

    public fun unregisterPlayer(player: Player) {
        require(player.slotId != PathingEntity.INVALID_SLOT) {
            "Invalid `slotId` for player: $player"
        }
        val slot = player.slotId
        player.slotId = -1
        player.destroy()
        players.remove(slot)
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

    public fun Player.teleport(dest: CoordGrid) {
        allocZoneCollision(dest)
        coords = dest
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

    public fun Player.opLoc1(loc: BoundLocInfo) {
        locInteractions.interact(this, loc, InteractionOp.Op1)
    }

    public fun Player.opLoc2(loc: BoundLocInfo) {
        locInteractions.interact(this, loc, InteractionOp.Op2)
    }

    public fun Player.opLoc3(loc: BoundLocInfo) {
        locInteractions.interact(this, loc, InteractionOp.Op3)
    }

    public fun Player.opLoc4(loc: BoundLocInfo) {
        locInteractions.interact(this, loc, InteractionOp.Op4)
    }

    public fun Player.opLoc5(loc: BoundLocInfo) {
        locInteractions.interact(this, loc, InteractionOp.Op5)
    }

    public fun Player.opNpc1(npc: Npc) {
        npcInteractions.interact(this, npc, InteractionOp.Op1)
    }

    public fun Player.opNpc2(npc: Npc) {
        npcInteractions.interact(this, npc, InteractionOp.Op2)
    }

    public fun Player.opNpc3(npc: Npc) {
        npcInteractions.interact(this, npc, InteractionOp.Op3)
    }

    public fun Player.opNpc4(npc: Npc) {
        npcInteractions.interact(this, npc, InteractionOp.Op4)
    }

    public fun Player.opNpc5(npc: Npc) {
        npcInteractions.interact(this, npc, InteractionOp.Op5)
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

    public fun Player.moveGameClick(dest: CoordGrid) {
        allocZoneCollision(dest)
        val message = MoveGameClick(dest.x, dest.z, keyCombination = 0)
        captureClient.queue(gameClickHandler, message)
    }

    public fun Player.withProtectedAccess(action: suspend ProtectedAccess.() -> Unit) {
        protectedAccess.launch(this) { action() }
    }

    public fun Inventory.count(obj: ObjType): Int {
        return count(objTypes[obj])
    }

    public fun spawnNpc(coords: CoordGrid, type: UnpackedNpcType, init: Npc.() -> Unit = {}): Npc {
        val npc = Npc(type, coords).apply(init)
        val add = npcRegistry.add(npc)
        check(add.isSuccess()) { "Could not add npc: result=$add, npc=$npc" }
        return npc
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

        return boundLoc
    }

    public fun placeMapLoc(
        coords: CoordGrid,
        type: LocType,
        shape: LocShape = LocShape.CentrepieceStraight,
        angle: LocAngle = LocAngle.West,
    ): BoundLocInfo = placeMapLoc(coords, locTypes[type], shape, angle)

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
    ): UnpackedLocType = findLocTypes { it.isAssociatedWith(content) && predicate(it) }.first()

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
            registerPlayer(coords, this, startSlot, startClient)
        }
        return caught as T
    }

    public fun assertDoesNotThrow(msg: String, block: () -> Unit) {
        Assertions.assertDoesNotThrow({ block() }, msg)
    }

    public fun assertDoesNotThrow(block: () -> Unit) {
        Assertions.assertDoesNotThrow { block() }
    }

    private fun allocZoneCollision(coord: CoordGrid) {
        collision.allocateIfAbsent(coord.x, coord.z, coord.level)
    }

    public class Builder(state: GameTestState, private val scripts: Set<KClass<out PluginScript>>) {
        private val cacheTypes: TypeListMap = state.cacheTypes
        private val collisionMap: CollisionFlagMap = state.collision
        private val eventBus: EventBus by lazy { resolveEventBus(state.eventBus) }

        internal fun build(): GameTestScope {
            val module = TestModule(eventBus, cacheTypes, collisionMap)
            val injector = Guice.createInjector(module)
            bindScriptEvents(injector)
            return injector.getInstance(GameTestScope::class.java)
        }

        private fun bindScriptEvents(injector: Injector) {
            val context = injector.getInstance(ScriptContext::class.java)
            for (clazz in scripts) {
                val script = injector.getInstance(clazz.java)
                with(script) { context.startUp() }
            }
        }

        /**
         * Resolves the appropriate [EventBus] for the current test scope.
         *
         * If specific [PluginScript] classes are provided via [Builder.scripts], a new, isolated
         * [EventBus] instance is created to ensure that events are bound exclusively for the
         * current test scope. This prevents cross-contamination, where events from unrelated
         * plugins might interfere with this test scope, potentially causing unexpected results.
         *
         * If no plugins are specified, the default [EventBus] from the test state is used. This
         * default instance includes events bound by all available plugins.
         */
        private fun resolveEventBus(defaultEventBus: EventBus): EventBus {
            return if (scripts.isNotEmpty()) {
                EventBus()
            } else {
                defaultEventBus
            }
        }

        private class TestModule(
            private val eventBus: EventBus,
            private val cacheTypes: TypeListMap,
            private val gameCollisionMap: CollisionFlagMap,
        ) : AbstractModule() {
            override fun configure() {
                bind(EventBus::class.java).toInstance(eventBus)

                collisionFactory.borrowSharedMap().let { collision ->
                    // Copy the original game's collision flag map into the test.
                    // Important Note: This does _not_ add locs into the loc registry.
                    gameCollisionMap.flags.copyInto(collision.flags)
                    bind(CollisionFlagMap::class.java).toInstance(collision)
                }

                bind(GameExceptionHandler::class.java)
                    .to(TestExceptionHandler::class.java)
                    .`in`(Scopes.SINGLETON)

                bind(GameRandom::class.java)
                    .annotatedWith(CoreRandom::class.java)
                    .toInstance(DefaultGameRandom(seed = 123456))

                VariableGameRandom().let { random ->
                    bind(GameRandom::class.java).toInstance(random.impl)
                    bind(VariableGameRandom::class.java).toInstance(random)
                }

                bind(MapClock::class.java).`in`(Scopes.SINGLETON)
                bind(LocZoneStorage::class.java).`in`(Scopes.SINGLETON)

                bind(BoundValidator::class.java).`in`(Scopes.SINGLETON)
                bind(RayCastValidator::class.java).`in`(Scopes.SINGLETON)
                bind(RayCastFactory::class.java).`in`(Scopes.SINGLETON)
                bind(RouteFactory::class.java).`in`(Scopes.SINGLETON)
                bind(StepFactory::class.java).`in`(Scopes.SINGLETON)

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

                bind(TypeListMap::class.java).toInstance(cacheTypes)
                bind(ComponentTypeList::class.java).toInstance(cacheTypes.components)
                bind(EnumTypeList::class.java).toInstance(cacheTypes.enums)
                bind(FontMetricsTypeList::class.java).toInstance(cacheTypes.fonts)
                bind(InterfaceTypeList::class.java).toInstance(cacheTypes.interfaces)
                bind(InvTypeList::class.java).toInstance(cacheTypes.invs)
                bind(LocTypeList::class.java).toInstance(cacheTypes.locs)
                bind(NpcTypeList::class.java).toInstance(cacheTypes.npcs)
                bind(ObjTypeList::class.java).toInstance(cacheTypes.objs)
                bind(ParamTypeList::class.java).toInstance(cacheTypes.params)
                bind(SeqTypeList::class.java).toInstance(cacheTypes.seqs)
                bind(StatTypeList::class.java).toInstance(cacheTypes.stats)
                bind(SynthTypeList::class.java).toInstance(cacheTypes.synths)
                bind(VarBitTypeList::class.java).toInstance(cacheTypes.varbits)
                bind(VarpTypeList::class.java).toInstance(cacheTypes.varps)

                bind(CheatCommandMap::class.java).`in`(Scopes.SINGLETON)

                Multibinder.newSetBinder(binder(), InvisibleLevelMod::class.java)
                bind(InvisibleLevels::class.java).`in`(Scopes.SINGLETON)

                Multibinder.newSetBinder(binder(), XpMod::class.java)
                bind(XpModifiers::class.java).`in`(Scopes.SINGLETON)

                bind(MarketPrices::class.java).toInstance(DefaultMarketPrices)

                bind(EnumTypeMapResolver::class.java)
                    .toProvider(EnumTypeMapResolverProvider::class.java)
                    .`in`(Scopes.SINGLETON)
            }
        }

        private class EnumTypeMapResolverProvider
        @Inject
        constructor(private val enums: EnumTypeList) : Provider<EnumTypeMapResolver> {
            override fun get(): EnumTypeMapResolver = EnumTypeMapResolver(enums)
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
        public operator fun get(stat: StatType): Int = backing.getCurrentLevel(stat).toInt()

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
    }

    private class TestExceptionHandler : GameExceptionHandler {
        override fun handle(t: Throwable, msg: () -> Any?) {
            throw t
        }
    }
}
