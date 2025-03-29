package org.rsmod.api.spells.runes.fake

import org.rsmod.api.spells.runes.fake.configs.fake_enums
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.util.EnumTypeMapResolver

/**
 * Some features in the game use "fake" runes to ensure that, if players manage to smuggle them
 * outside their intended areas, they won't have unintended value or impact. For example, Barbarian
 * Assault provides runes during the game, but uses secondary "fake" variants instead of the
 * originals as a safeguard.
 */
public class FakeRuneRepository {
    private lateinit var fakes: Map<Int, ObjType>

    public operator fun get(rune: ObjType): ObjType? = fakes[rune.id]

    internal fun init(resolver: EnumTypeMapResolver) {
        val fakes = loadFakeRunes(resolver)
        this.fakes = fakes
    }

    private fun loadFakeRunes(resolver: EnumTypeMapResolver): Map<Int, ObjType> {
        val enum = resolver[fake_enums.runes].filterValuesNotNull()
        return enum.backing.entries.associate { it.key.id to it.value }
    }
}
