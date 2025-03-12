package org.rsmod.api.player.protect

import jakarta.inject.Inject
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.dialogue.Dialogues
import org.rsmod.api.player.hit.processor.InstantPlayerHitProcessor
import org.rsmod.api.player.interact.HeldInteractions
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.WornInteractions
import org.rsmod.api.random.GameRandom
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.events.EventBus
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.routefinder.collision.CollisionFlagMap

public class ProtectedAccessContextFactory
@Inject
constructor(
    private val random: GameRandom,
    private val collision: CollisionFlagMap,
    private val dialogues: Dialogues,
    private val eventBus: EventBus,
    private val invTypes: InvTypeList,
    private val locTypes: LocTypeList,
    private val npcTypes: NpcTypeList,
    private val objTypes: ObjTypeList,
    private val seqTypes: SeqTypeList,
    private val locInteractions: LocInteractions,
    private val npcInteractions: NpcInteractions,
    private val heldInteractions: HeldInteractions,
    private val wornInteractions: WornInteractions,
    private val marketPrices: MarketPrices,
    private val invisibleLevels: InvisibleLevels,
    private val instantHitProcessor: InstantPlayerHitProcessor,
) {
    public fun create(): ProtectedAccessContext =
        ProtectedAccessContext(
            getRandom = { random },
            getEventBus = { eventBus },
            getCollision = { collision },
            getDialogues = { dialogues },
            getInvTypes = { invTypes },
            getLocTypes = { locTypes },
            getNpcTypes = { npcTypes },
            getObjTypes = { objTypes },
            getSeqTypes = { seqTypes },
            getLocInteractions = { locInteractions },
            getNpcInteractions = { npcInteractions },
            getHeldInteractions = { heldInteractions },
            getWornInteractions = { wornInteractions },
            getMarketPrices = { marketPrices },
            getInvisibleLevels = { invisibleLevels },
            getInstantHitProcessor = { instantHitProcessor },
        )

    public companion object {
        private val EMPTY_CTX: ProtectedAccessContext =
            ProtectedAccessContext(
                getRandom = { error("No game random provided.") },
                getEventBus = { error("No event bus provided.") },
                getCollision = { error("No collision map provided.") },
                getDialogues = { error("No dialogues provided.") },
                getInvTypes = { error("No inv type list provided.") },
                getLocTypes = { error("No loc type list provided.") },
                getNpcTypes = { error("No npc type list provided.") },
                getObjTypes = { error("No obj type list provided.") },
                getSeqTypes = { error("No seq type list provided.") },
                getLocInteractions = { error("No loc interactions provided.") },
                getNpcInteractions = { error("No npc interactions provided.") },
                getHeldInteractions = { error("No held interactions provided.") },
                getWornInteractions = { error("No worn interactions provided.") },
                getMarketPrices = { error("No market prices provided.") },
                getInvisibleLevels = { error("No invisible levels provided.") },
                getInstantHitProcessor = { error("No instant hit processor provided.") },
            )

        /**
         * Returns a lightweight, empty context for use when a full [ProtectedAccessContext] is not
         * required.
         *
         * ## Usage:
         * - Use in cases where the [ProtectedAccessContext] dependencies are not needed within the
         *   scope.
         * - Accessing any context-dependent properties will throw an [IllegalStateException].
         *
         * _Excessive use of [EMPTY_CTX] may indicate improper usage of [ProtectedAccessContext]._
         */
        public fun empty(): ProtectedAccessContext = EMPTY_CTX
    }
}
