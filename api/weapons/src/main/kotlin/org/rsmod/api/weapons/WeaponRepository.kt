package org.rsmod.api.weapons

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList

public class WeaponRepository
@Inject
constructor(private val objTypes: ObjTypeList, private val registry: WeaponRegistry) {
    private val mappedContentGroups by lazy { loadMappedContentGroups() }

    public fun <T : CombatAttack> register(obj: ObjType, weapon: Weapon<T>) {
        val result = registry.add(obj, weapon)
        assertValidResult(obj, result)
    }

    public fun <T : CombatAttack> register(content: ContentGroupType, weapon: Weapon<T>) {
        val weapons = mappedContentGroups[content.id] ?: emptyList()
        require(weapons.isNotEmpty()) { "No weapons associated with content group: $content" }
        for (obj in weapons) {
            registry.add(obj, weapon)
        }
    }

    public fun <T : CombatAttack> replace(obj: ObjType, weapon: Weapon<T>) {
        registry.remove(obj)
        registry.add(obj, weapon)
    }

    private fun loadMappedContentGroups(): Map<Int, List<ObjType>> {
        val categorized = objTypes.values.filter { it.contentGroup != -1 && it.weaponCategory != 0 }
        return categorized.groupBy { it.contentGroup }
    }

    private fun assertValidResult(weapon: ObjType, result: WeaponRegistry.Result.Add) {
        when (result) {
            WeaponRegistry.Result.Add.AlreadyAdded -> error("Weapon already mapped: $weapon")
            WeaponRegistry.Result.Add.Success -> {
                /* no-op */
            }
        }
    }
}
