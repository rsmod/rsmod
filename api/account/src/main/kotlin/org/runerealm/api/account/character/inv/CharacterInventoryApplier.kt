package org.runerealm.api.account.character.inv

import jakarta.inject.Inject
import kotlin.collections.iterator
import org.runerealm.api.account.character.CharacterDataStage
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.util.UncheckedType

@OptIn(UncheckedType::class)
public class CharacterInventoryApplier @Inject constructor(private val invTypes: InvTypeList) :
    CharacterDataStage.Applier<CharacterInventoryData> {
    override fun apply(player: Player, data: CharacterInventoryData) {
        for (loaded in data.inventories) {
            val type = invTypes.getValue(loaded.type)
            val inventory = player.invMap.getOrPut(type)

            for ((slot, obj) in loaded.objs) {
                val (type, count, vars) = obj
                inventory[slot] = InvObj(type, count, vars = vars)
            }
        }
    }
}
