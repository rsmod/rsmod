package org.rsmod.api.combat.types

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import jakarta.inject.Inject
import org.rsmod.api.combat.player.righthand
import org.rsmod.api.combat.types.configs.type_enums
import org.rsmod.api.config.refs.varps
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.WeaponCategory
import org.rsmod.game.type.util.EnumTypeMapResolver

public class AttackTypes
@Inject
constructor(private val objTypes: ObjTypeList, private val enumResolver: EnumTypeMapResolver) {
    private lateinit var weaponTypes: WeaponTypeMap

    public fun get(player: Player): AttackType? {
        val type = player.righthand?.let(objTypes::get)
        val selection = player.vars[varps.attackstyle]
        return resolve(type = type, styleSelection = selection)
    }

    public fun resolve(type: UnpackedObjType?, styleSelection: Int): AttackType? {
        val weapon = WeaponCategory.getOrUnarmed(type?.weaponCategory)
        return resolve(weapon = weapon, styleSelection = styleSelection)
    }

    public fun resolve(weapon: WeaponCategory, styleSelection: Int): AttackType? {
        require(styleSelection in 0..3) { "Style selection must be within range [0..3]" }
        val types = weaponTypes[weapon]
        return types[styleSelection]
    }

    internal fun startUp() {
        val weaponTypes = loadWeaponTypeMap()
        this.weaponTypes = weaponTypes
    }

    private fun loadWeaponTypeMap(): WeaponTypeMap {
        val typesEnum = enumResolver[type_enums.weapon_attack_types].filterValuesNotNull()
        return WeaponTypeMap(Int2IntOpenHashMap(typesEnum.backing))
    }

    private data class WeaponTypeList(
        val one: AttackType?,
        val two: AttackType?,
        val three: AttackType?,
        val four: AttackType?,
    ) {
        operator fun get(index: Int): AttackType? =
            when (index) {
                0 -> one
                1 -> two
                2 -> three
                3 -> four
                else -> throw IndexOutOfBoundsException("Invalid index: $index")
            }
    }

    private class WeaponTypeMap(private val backing: Int2IntOpenHashMap = Int2IntOpenHashMap()) {
        operator fun get(weapon: WeaponCategory): WeaponTypeList {
            val packedStyles = backing[weapon.id]
            if (packedStyles == backing.defaultReturnValue()) {
                return WeaponTypeList(null, null, null, null)
            }
            val styles = PackedTypes(packedStyles)
            val (style1, style2, style3, style4) = styles
            return WeaponTypeList(style1, style2, style3, style4)
        }
    }
}
