package org.rsmod.api.cache.types.inv

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.cache.types.testBuf
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType
import org.rsmod.game.type.inv.InvStock
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.inv.UnpackedInvType
import org.rsmod.game.type.util.UncheckedType

class InvTypeCodecTest {
    @Test
    fun `encode and decode js5`() {
        val type = createJs5InvType()
        val encoded = testBuf().encodeConfig { InvTypeEncoder.encodeJs5(type, this) }
        val decoded = InvTypeDecoder.decode(encoded).build(type.id)
        assertEquals(type, decoded)
    }

    @Test
    fun `encode and decode full`() {
        val type = createGameInvType()
        val encoded = testBuf().apply { InvTypeEncoder.encodeFull(type, this) }
        val decoded = InvTypeDecoder.decode(encoded).build(type.id)
        assertEquals(type, decoded)
    }

    private fun createJs5InvType(): UnpackedInvType =
        UnpackedInvType(
            scope = InvTypeBuilder.DEFAULT_SCOPE,
            stack = InvTypeBuilder.DEFAULT_STACK,
            size = 25,
            protect = true,
            allStock = false,
            restock = false,
            runWeight = false,
            dummyInv = false,
            placeholders = false,
            stock = null,
            internalId = 0,
            internalName = "null",
        )

    @OptIn(UncheckedType::class)
    private fun createGameInvType(): UnpackedInvType =
        UnpackedInvType(
            scope = InvScope.Shared,
            stack = InvStackType.Always,
            size = 30,
            protect = true,
            allStock = false,
            restock = false,
            runWeight = true,
            dummyInv = true,
            placeholders = true,
            stock =
                arrayOf(
                    InvStock(1, 2, 3),
                    InvStock(4, 5, 6),
                    null,
                    InvStock(7, 0, 8),
                    InvStock(9, Int.MAX_VALUE, 10),
                ),
            internalId = 0,
            internalName = "null",
        )
}
