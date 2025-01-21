package org.rsmod.api.player.protect

import jakarta.inject.Inject
import org.rsmod.api.player.interact.HeldInteractions
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.random.GameRandom
import org.rsmod.events.EventBus
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.routefinder.collision.CollisionFlagMap

public class ProtectedAccessContextFactory
@Inject
constructor(
    private val random: GameRandom,
    private val collision: CollisionFlagMap,
    private val eventBus: EventBus,
    private val npcTypes: NpcTypeList,
    private val objTypes: ObjTypeList,
    private val seqTypes: SeqTypeList,
    private val locInteractions: LocInteractions,
    private val heldInteractions: HeldInteractions,
) {
    public fun create(): ProtectedAccessContext =
        ProtectedAccessContext(
            getRandom = { random },
            getEventBus = { eventBus },
            getCollision = { collision },
            getNpcTypes = { npcTypes },
            getObjTypes = { objTypes },
            getSeqTypes = { seqTypes },
            getLocInteractions = { locInteractions },
            getHeldInteractions = { heldInteractions },
        )

    public companion object {
        private val EMPTY_CTX: ProtectedAccessContext =
            ProtectedAccessContext(
                getRandom = { error("No game random provided.") },
                getEventBus = { error("No event bus provided.") },
                getCollision = { error("No collision map provided.") },
                getNpcTypes = { error("No npc type list provided.") },
                getObjTypes = { error("No obj type list provided.") },
                getSeqTypes = { error("No seq type list provided.") },
                getLocInteractions = { error("No loc interactions provided.") },
                getHeldInteractions = { error("No held interactions provided.") },
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
