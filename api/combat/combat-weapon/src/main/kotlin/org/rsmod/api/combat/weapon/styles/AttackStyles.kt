package org.rsmod.api.combat.weapon.styles

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import jakarta.inject.Inject
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.weapon.righthand
import org.rsmod.api.combat.weapon.styles.configs.style_enums
import org.rsmod.api.config.refs.varps
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.WeaponCategory
import org.rsmod.game.type.util.EnumTypeMapResolver

public class AttackStyles
@Inject
constructor(private val objTypes: ObjTypeList, private val enumResolver: EnumTypeMapResolver) {
    private lateinit var weaponStyles: WeaponStyleMap

    public fun get(player: Player): AttackStyle? {
        val type = objTypes.getOrNull(player.righthand)
        val stance = player.vars[varps.attackstyle]
        return resolve(type = type, combatStance = stance)
    }

    public fun resolve(type: UnpackedObjType?, combatStance: Int): AttackStyle? {
        val weapon = WeaponCategory.getOrUnarmed(type?.weaponCategory)
        return resolve(weapon = weapon, combatStance = combatStance)
    }

    public fun resolve(weapon: WeaponCategory, combatStance: Int): AttackStyle? {
        require(combatStance in 0..3) { "Combat stance must be within range [0..3]" }
        val styles = weaponStyles[weapon]
        return styles[combatStance]
    }

    internal fun startUp() {
        val weaponStyles = loadWeaponStylesMap()
        this.weaponStyles = weaponStyles
    }

    private fun loadWeaponStylesMap(): WeaponStyleMap {
        val stylesEnum = enumResolver[style_enums.weapon_attack_styles].filterValuesNotNull()
        return WeaponStyleMap(Int2IntOpenHashMap(stylesEnum.backing))
    }

    private data class WeaponStyleList(
        val one: AttackStyle?,
        val two: AttackStyle?,
        val three: AttackStyle?,
        val four: AttackStyle?,
    ) {
        operator fun get(index: Int): AttackStyle? =
            when (index) {
                0 -> one
                1 -> two
                2 -> three
                3 -> four
                else -> throw IndexOutOfBoundsException("Invalid index: $index")
            }
    }

    private class WeaponStyleMap(private val backing: Int2IntOpenHashMap = Int2IntOpenHashMap()) {
        operator fun get(weapon: WeaponCategory): WeaponStyleList {
            val packedStyles = backing[weapon.id]
            if (packedStyles == backing.defaultReturnValue()) {
                return WeaponStyleList(null, null, null, null)
            }
            val styles = PackedStyles(packedStyles)
            val (style1, style2, style3, style4) = styles
            return WeaponStyleList(style1, style2, style3, style4)
        }
    }
}
