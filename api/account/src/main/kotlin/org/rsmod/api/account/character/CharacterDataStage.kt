package org.rsmod.api.account.character

import org.rsmod.api.account.loader.AccountLoaderService
import org.rsmod.api.db.Database
import org.rsmod.game.entity.Player
import org.rsmod.module.ExtendedModule

public class CharacterDataStage {
    /**
     * Marker interface for data segments that represent individual components of a character's
     * saved state.
     *
     * Each [Segment] is intended to encapsulate a specific type of data (e.g., inventory, stats,
     * appearance) and is later consumed by an [Applier] during player initialization.
     */
    public interface Segment

    /**
     * A data pipeline defines how to load and initialize character-related data from the database,
     * converting it into segments ([Segment]) that the server can apply to the player.
     *
     * Pipelines should either load data from the database (`append`) or initialize it in memory
     * (`create`) during account creation. The produced segments must be registered via
     * [CharacterMetadataList.add] along with their matching [Applier].
     *
     * _Note: Implementations must be registered using [ExtendedModule.addSetBinding] in order to be
     * discovered by [AccountLoaderService]._
     */
    public interface Pipeline {
        /**
         * Called when an existing character is being loaded from the database.
         *
         * Implementations should query the [database] for related data and append the resulting
         * [Segment]s to the provided [metadata] list.
         *
         * This is typically used to load persisted state (inventory, stats, etc.) during login.
         */
        public suspend fun append(database: Database, metadata: CharacterMetadataList)

        /**
         * Called when a character is being saved to the database.
         *
         * Implementations should persist the current state of the [player] to the database, using
         * the associated [characterId] as the reference for storage.
         *
         * This is typically used during logout or periodic save events to write back any modified
         * data (such as inventories, stats, etc.).
         */
        public suspend fun save(database: Database, player: Player, characterId: Int)
    }

    /**
     * Responsible for applying a specific [Segment] to a [Player] instance.
     *
     * Each [Applier] is associated with a specific segment type and contains the logic to mutate
     * the player's state based on the segment's data. It is invoked during player login, after the
     * player instance is created but before they are registered to the world.
     *
     * For example, this might set the player's inventory, levels, appearance, etc.
     */
    public interface Applier<T : Segment> {
        /**
         * Apply the given [data] to the provided [player]. Called from the game thread.
         *
         * Implementors should throw exceptions if application fails in an unrecoverable way. Such
         * failures are caught by the server and typically result in the player's login session
         * being terminated.
         */
        public fun apply(player: Player, data: T)
    }
}
