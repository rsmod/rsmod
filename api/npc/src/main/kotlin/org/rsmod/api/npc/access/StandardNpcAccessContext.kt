package org.rsmod.api.npc.access

import org.rsmod.api.npc.hit.modifier.NpcHitModifier
import org.rsmod.api.npc.hit.processor.NpcHitProcessor
import org.rsmod.api.random.GameRandom

/**
 * Provides the minimal contextual dependencies required for operations within the
 * [StandardNpcAccess] scope for npcs.
 *
 * This context is analogous to `ProtectedAccessContext` used for players but is intentionally kept
 * lightweight. Since there can be exponentially more npcs than players at any given time,
 * minimizing the overhead for each npc's access scope is essential for performance.
 *
 * ## Design Rationale:
 * - **Lightweight**: The [StandardNpcAccessContext] includes only the essential dependencies needed
 *   for standard npc actions. This minimal design helps keep the memory footprint as small as
 *   possible when many npcs are active.
 * - **Consistency**: By providing a similar action interface as `ProtectedAccess` for players, the
 *   system remains consistent for developers working with both players and npcs. However, unlike
 *   players, npcs do not require a full suite of dependencies.
 * - **Extensibility**: Although minimal by design, additional dependencies can be considered in the
 *   future if the need arises. Any such additions should be carefully evaluated to ensure that they
 *   do not compromise the lightweight nature of the npc context.
 * - **Separation of Concerns**: For more specialized behavior or additional dependencies beyond
 *   what this class provides, it is recommended to inject those directly into the plugin script
 *   handling the npc logic rather than expanding this context.
 */
public data class StandardNpcAccessContext(
    private val getRandom: () -> GameRandom,
    private val getHitModifier: () -> NpcHitModifier,
    private val getHitProcessor: () -> NpcHitProcessor,
) {
    public val random: GameRandom by lazyLoad { getRandom() }
    public val hitModifier: NpcHitModifier by lazyLoad { getHitModifier() }
    public val hitProcessor: NpcHitProcessor by lazyLoad { getHitProcessor() }
}

private fun <T> lazyLoad(init: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, init)
