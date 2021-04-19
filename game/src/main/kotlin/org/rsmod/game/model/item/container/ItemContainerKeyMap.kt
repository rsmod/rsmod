package org.rsmod.game.model.item.container

import com.github.michaelbull.logging.InlineLogger

private val logger = InlineLogger()

class ItemContainerKeyMap(
    private val containerKeys: MutableMap<String, ItemContainerKey> = mutableMapOf()
) : Map<String, ItemContainerKey> by containerKeys {

    fun register(key: ItemContainerKey) {
        check(!containerKeys.containsKey(key.name)) { "Container with key \"${key.name}\" already exists." }
        logger.debug { "Register item container key (key=$key)" }
        containerKeys[key.name] = key
    }
}
