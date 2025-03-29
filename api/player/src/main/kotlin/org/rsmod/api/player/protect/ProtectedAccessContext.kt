package org.rsmod.api.player.protect

import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.dialogue.Dialogues
import org.rsmod.api.player.hit.processor.InstantPlayerHitProcessor
import org.rsmod.api.player.interact.HeldInteractions
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.WornInteractions
import org.rsmod.api.random.GameRandom
import org.rsmod.events.EventBus
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.routefinder.collision.CollisionFlagMap

/**
 * Provides contextual access to external systems required by the [ProtectedAccess] scope.
 *
 * This class allows the implicit passing of certain dependencies (e.g., [EventBus]) to functions
 * within the [ProtectedAccess] scope, reducing the burden on developers to inject or explicitly
 * pass these dependencies in every instance where they are required.
 *
 * ## Design Decision:
 * The [ProtectedAccessContext] class was introduced to balance ease of use and flexibility in the
 * framework. While explicitly passing dependencies (e.g., through function parameters) is often
 * considered best practice, this can become cumbersome when frequently interacting with multiple
 * systems, such as ui-related functions and movement (more specifically, teleporting). The context
 * provides these dependencies by default, but still allows consumers to override them if necessary.
 *
 * ### Why Lazy Initialization?
 * Context dependencies are initialized lazily. This ensures that they are only instantiated when
 * needed, improving performance in cases where certain systems are not required during the lifetime
 * of a particular interaction.
 *
 * **Note**: The lazy initialization assumes single-threaded usage, as [LazyThreadSafetyMode.NONE]
 * is used. If you're extending the context to work in a multithreaded environment, you may need to
 * adapt this behavior accordingly.
 *
 * ## Usage:
 * In most cases, this class will be used implicitly in the [ProtectedAccess] scope. Developers
 * should generally not need to interact with the context directly, as it provides sensible defaults
 * for common use cases. However, for specialized behavior (e.g., testing, custom logic), developers
 * can override the default context by passing their own dependencies.
 *
 * ### Extensibility:
 * The context was designed with flexibility in mind, allowing additional dependencies to be added
 * in the future without affecting existing code.
 *
 * ### Example:
 * ```
 * val customContext = ProtectedAccessContext(
 *     getEventBus = { customEventBus },
 *     getCollision = { customCollisionMap },
 *     ...
 * )
 *
 * player.withProtectedAccess(customContext) { ... }
 * ```
 *
 * Or
 *
 * ```
 * protectedAccess.telejump(dest, collision = customCollisionMap)
 * ```
 */
public data class ProtectedAccessContext(
    private val getRandom: () -> GameRandom,
    private val getEventBus: () -> EventBus,
    private val getNpcList: () -> NpcList,
    private val getPlayerList: () -> PlayerList,
    private val getCollision: () -> CollisionFlagMap,
    private val getDialogues: () -> Dialogues,
    private val getInvTypes: () -> InvTypeList,
    private val getLocTypes: () -> LocTypeList,
    private val getNpcTypes: () -> NpcTypeList,
    private val getObjTypes: () -> ObjTypeList,
    private val getSeqTypes: () -> SeqTypeList,
    private val getLocInteractions: () -> LocInteractions,
    private val getNpcInteractions: () -> NpcInteractions,
    private val getHeldInteractions: () -> HeldInteractions,
    private val getWornInteractions: () -> WornInteractions,
    private val getMarketPrices: () -> MarketPrices,
    private val getInstantHitProcessor: () -> InstantPlayerHitProcessor,
) {
    public val random: GameRandom by lazyLoad { getRandom() }
    public val eventBus: EventBus by lazyLoad { getEventBus() }
    public val npcList: NpcList by lazyLoad { getNpcList() }
    public val playerList: PlayerList by lazyLoad { getPlayerList() }
    public val collision: CollisionFlagMap by lazyLoad { getCollision() }
    public val dialogues: Dialogues by lazyLoad { getDialogues() }
    public val invTypes: InvTypeList by lazyLoad { getInvTypes() }
    public val locTypes: LocTypeList by lazyLoad { getLocTypes() }
    public val npcTypes: NpcTypeList by lazyLoad { getNpcTypes() }
    public val objTypes: ObjTypeList by lazyLoad { getObjTypes() }
    public val seqTypes: SeqTypeList by lazyLoad { getSeqTypes() }
    public val locInteractions: LocInteractions by lazyLoad { getLocInteractions() }
    public val npcInteractions: NpcInteractions by lazyLoad { getNpcInteractions() }
    public val heldInteractions: HeldInteractions by lazyLoad { getHeldInteractions() }
    public val wornInteractions: WornInteractions by lazyLoad { getWornInteractions() }
    public val marketPrices: MarketPrices by lazyLoad { getMarketPrices() }
    public val instantHitProcessor: InstantPlayerHitProcessor by lazyLoad {
        getInstantHitProcessor()
    }
}

private fun <T> lazyLoad(init: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, init)
