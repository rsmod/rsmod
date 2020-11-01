package org.rsmod.game.model.item.type

private const val DEFAULT_ID = -1
private const val DEFAULT_NAME = "null"
private const val DEFAULT_COST = 0
private const val DEFAULT_STACKS = false
private const val DEFAULT_MEMBERS = false
private const val DEFAULT_EXCHANGEABLE = false
private const val DEFAULT_TEAM_CAPE = 0
private const val DEFAULT_NOTE_LINK = 0
private const val DEFAULT_NOTE_VALUE = 0
private const val DEFAULT_PLACEHOLDER_LINK = 0
private const val DEFAULT_PLACEHOLDER_VALUE = 0
private val DEFAULT_GROUND_OPTIONS = emptyArray<String?>()
private val DEFAULT_INVENTORY_OPTIONS = emptyArray<String?>()
private val DEFAULT_PARAMETERS = emptyMap<Int, Any>()

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class ItemTypeBuilder(
    var id: Int = DEFAULT_ID,
    var name: String = DEFAULT_NAME,
    var cost: Int = DEFAULT_COST,
    var stacks: Boolean = DEFAULT_STACKS,
    var members: Boolean = DEFAULT_MEMBERS,
    var groundOptions: Array<String?> = DEFAULT_GROUND_OPTIONS,
    var inventoryOptions: Array<String?> = DEFAULT_INVENTORY_OPTIONS,
    var exchangeable: Boolean = DEFAULT_EXCHANGEABLE,
    var teamCape: Int = DEFAULT_TEAM_CAPE,
    var noteLink: Int = DEFAULT_NOTE_LINK,
    var noteValue: Int = DEFAULT_NOTE_VALUE,
    var placeholderLink: Int = DEFAULT_PLACEHOLDER_LINK,
    var placeholderValue: Int = DEFAULT_PLACEHOLDER_VALUE,
    var parameters: Map<Int, Any> = DEFAULT_PARAMETERS
) {

    val defaultGroundOps: Boolean
        get() = groundOptions === DEFAULT_GROUND_OPTIONS

    val defaultInventoryOps: Boolean
        get() = inventoryOptions === DEFAULT_INVENTORY_OPTIONS

    fun build(): ItemType {
        check(id != DEFAULT_ID) { "Item type id has not been set." }
        return ItemType(
            id = id,
            name = name,
            cost = cost,
            stacks = stacks,
            members = members,
            groundOptions = groundOptions.toList(),
            inventoryOptions = inventoryOptions.toList(),
            exchangeable = exchangeable,
            teamCape = teamCape,
            noteLink = noteLink,
            noteValue = noteValue,
            placeholderLink = placeholderLink,
            placeholderValue = placeholderValue,
            parameters = parameters
        )
    }
}
