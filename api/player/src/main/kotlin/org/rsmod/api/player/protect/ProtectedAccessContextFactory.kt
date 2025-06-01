package org.rsmod.api.player.protect

import jakarta.inject.Inject
import org.rsmod.api.area.checker.AreaChecker
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.dialogue.align.TextAlignment
import org.rsmod.api.player.hit.processor.InstantPlayerHitProcessor
import org.rsmod.api.player.interact.HeldInteractions
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.PlayerInteractions
import org.rsmod.api.player.interact.WornInteractions
import org.rsmod.api.player.music.MusicPlayer
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

public class ProtectedAccessContextFactory
@Inject
constructor(
    private val random: GameRandom,
    private val eventBus: EventBus,
    private val npcList: NpcList,
    private val playerList: PlayerList,
    private val collision: CollisionFlagMap,
    private val areaChecker: AreaChecker,
    private val alignment: TextAlignment,
    private val invTypes: InvTypeList,
    private val locTypes: LocTypeList,
    private val npcTypes: NpcTypeList,
    private val objTypes: ObjTypeList,
    private val seqTypes: SeqTypeList,
    private val locInteractions: LocInteractions,
    private val npcInteractions: NpcInteractions,
    private val playerInteractions: PlayerInteractions,
    private val heldInteractions: HeldInteractions,
    private val wornInteractions: WornInteractions,
    private val musicPlayer: MusicPlayer,
    private val marketPrices: MarketPrices,
    private val instantHitProcessor: InstantPlayerHitProcessor,
) {
    public fun create(): ProtectedAccessContext =
        ProtectedAccessContext(
            getRandom = { random },
            getEventBus = { eventBus },
            getNpcList = { npcList },
            getPlayerList = { playerList },
            getCollision = { collision },
            getAreaChecker = { areaChecker },
            getAlignment = { alignment },
            getInvTypes = { invTypes },
            getLocTypes = { locTypes },
            getNpcTypes = { npcTypes },
            getObjTypes = { objTypes },
            getSeqTypes = { seqTypes },
            getLocInteractions = { locInteractions },
            getNpcInteractions = { npcInteractions },
            getPlayerInteractions = { playerInteractions },
            getHeldInteractions = { heldInteractions },
            getWornInteractions = { wornInteractions },
            getMusicPlayer = { musicPlayer },
            getMarketPrices = { marketPrices },
            getInstantHitProcessor = { instantHitProcessor },
        )

    public companion object {
        private val EMPTY_CTX: ProtectedAccessContext =
            ProtectedAccessContext(
                getRandom = { error("No game random provided.") },
                getEventBus = { error("No event bus provided.") },
                getNpcList = { error("No npc list provided.") },
                getPlayerList = { error("No player list provided.") },
                getCollision = { error("No collision map provided.") },
                getAreaChecker = { error("No area checker provided.") },
                getAlignment = { error("No text alignment provided.") },
                getInvTypes = { error("No inv type list provided.") },
                getLocTypes = { error("No loc type list provided.") },
                getNpcTypes = { error("No npc type list provided.") },
                getObjTypes = { error("No obj type list provided.") },
                getSeqTypes = { error("No seq type list provided.") },
                getLocInteractions = { error("No loc interactions provided.") },
                getNpcInteractions = { error("No npc interactions provided.") },
                getPlayerInteractions = { error("No player interactions provided.") },
                getHeldInteractions = { error("No held interactions provided.") },
                getWornInteractions = { error("No worn interactions provided.") },
                getMusicPlayer = { error("No music player provided.") },
                getMarketPrices = { error("No market prices provided.") },
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
