package org.rsmod.api.account.character.inv

import jakarta.inject.Inject
import org.rsmod.api.account.character.CharacterDataStage
import org.rsmod.api.account.character.CharacterMetadataList
import org.rsmod.api.db.Database
import org.rsmod.api.db.util.prepareStatement
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.inv.InvScope

private typealias CharacterInventory = CharacterInventoryData.Inventory

private typealias CharacterObj = CharacterInventoryData.Obj

public class CharacterInventoryPipeline
@Inject
constructor(private val applier: CharacterInventoryApplier) : CharacterDataStage.Pipeline {
    override suspend fun append(database: Database, metadata: CharacterMetadataList) {
        val inventories = selectInventories(database, metadata)

        // Avoid malformed query if no inventories exist.
        if (inventories.isEmpty()) {
            metadata.add(applier, CharacterInventoryData(inventories))
            return
        }

        val rowInventories = inventories.associateBy { it.rowId }

        val placeholders = (0 until inventories.size).joinToString(",") { "?" }
        val select =
            database.prepareStatement(
                """
                    SELECT inventories_id, slot, obj, count, vars
                    FROM inventory_objs
                    WHERE inventories_id IN ($placeholders)
                """
                    .trimIndent()
            )

        select.use {
            inventories.forEachIndexed { index, inventory -> it.setInt(1 + index, inventory.rowId) }
            it.executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    val inventoriesRow = resultSet.getInt("inventories_id")
                    val slot = resultSet.getInt("slot")
                    val obj = resultSet.getInt("obj")
                    val count = resultSet.getInt("count")
                    val vars = resultSet.getInt("vars")

                    val inventory = rowInventories.getValue(inventoriesRow)
                    inventory.objs[slot] = CharacterObj(obj, count, vars)
                }
            }
        }

        metadata.add(applier, CharacterInventoryData(inventories))
    }

    private suspend fun selectInventories(
        database: Database,
        metadata: CharacterMetadataList,
    ): List<CharacterInventory> {
        val inventories = ArrayList<CharacterInventory>(4)

        val select =
            database.prepareStatement(
                """
                    SELECT id, inv_type
                    FROM inventories
                    WHERE character_id = ?
                """
                    .trimIndent()
            )

        select.use {
            it.setInt(1, metadata.characterId)
            it.executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    val id = resultSet.getInt("id")
                    val type = resultSet.getInt("inv_type")
                    inventories += CharacterInventory(rowId = id, type = type)
                }
            }
        }

        return inventories
    }

    override suspend fun save(database: Database, player: Player, characterId: Int) {
        val persistentInvs = player.invMap.values.filter { it.type.scope == InvScope.Perm }
        deleteStaleInventories(database, characterId, persistentInvs)

        val delete =
            database.prepareStatement(
                """
                    DELETE FROM inventory_objs
                    WHERE inventories_id = ? AND slot = ?
                """
                    .trimIndent()
            )

        val upsert =
            database.prepareStatement(
                """
                    INSERT OR REPLACE INTO
                        inventory_objs (inventories_id, slot, obj, count, vars)
                    VALUES (?, ?, ?, ?, ?)
                """
                    .trimIndent()
            )

        delete.use { delete ->
            upsert.use { upsert ->
                for (inventory in persistentInvs) {
                    val type = inventory.type

                    val inventoryRowId = getOrInsertInventoryRowId(database, characterId, type.id)
                    if (inventoryRowId == null) {
                        val message =
                            "Fatal error fetching inventory row for: $type (player=$player)"
                        throw IllegalStateException(message)
                    }

                    for (i in inventory.indices) {
                        if (inventory[i] == null) {
                            delete.setInt(1, inventoryRowId)
                            delete.setInt(2, i)
                            delete.addBatch()
                        }
                    }

                    for (i in inventory.indices) {
                        val obj = inventory[i]
                        if (obj != null) {
                            upsert.setInt(1, inventoryRowId)
                            upsert.setInt(2, i)
                            upsert.setInt(3, obj.id)
                            upsert.setInt(4, obj.count)
                            upsert.setInt(5, obj.vars)
                            upsert.addBatch()
                        }
                    }
                }
                delete.executeBatch()
                upsert.executeBatch()
            }
        }
    }

    private suspend fun deleteStaleInventories(
        database: Database,
        characterId: Int,
        inventories: Collection<Inventory>,
    ) {
        // Important: This function assumes `inventory_objs` references `inventories` with
        // `ON DELETE CASCADE`, so deleting a parent inventory also deletes its associated
        // `inventory_objs` rows.
        if (inventories.isNotEmpty()) {
            val activeInvPlaceholders = (0 until inventories.size).joinToString(",") { "?" }
            val deleteStaleInventories =
                database.prepareStatement(
                    """
                        DELETE FROM inventories
                        WHERE character_id = ? AND inv_type NOT IN ($activeInvPlaceholders)
                    """
                        .trimIndent()
                )

            deleteStaleInventories.use {
                it.setInt(1, characterId)
                inventories.forEachIndexed { index, inv -> it.setInt(2 + index, inv.type.id) }
                it.executeUpdate()
            }
        } else {
            val deleteAllInventories =
                database.prepareStatement("DELETE FROM inventories WHERE character_id = ?")

            deleteAllInventories.use {
                it.setInt(1, characterId)
                it.executeUpdate()
            }
        }
    }

    private suspend fun getOrInsertInventoryRowId(
        database: Database,
        characterId: Int,
        invType: Int,
    ): Int? {
        val insert =
            database.prepareStatement(
                """
                    INSERT OR IGNORE INTO inventories (character_id, inv_type)
                    VALUES (?, ?)
                """
                    .trimIndent()
            )

        insert.use {
            it.setInt(1, characterId)
            it.setInt(2, invType)
            it.executeUpdate()
        }

        val select =
            database.prepareStatement(
                """
                    SELECT id FROM inventories
                    WHERE character_id = ? AND inv_type = ?
                """
                    .trimIndent()
            )

        select.use {
            it.setInt(1, characterId)
            it.setInt(2, invType)
            val rowId =
                select.executeQuery().use { rs ->
                    if (rs.next()) {
                        rs.getInt("id")
                    } else {
                        null
                    }
                }
            return rowId
        }
    }
}
