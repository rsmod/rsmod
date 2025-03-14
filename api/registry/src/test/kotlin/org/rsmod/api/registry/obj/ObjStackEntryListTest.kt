package org.rsmod.api.registry.obj

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.factory.obj.TestObjFactory
import org.rsmod.api.testing.factory.obj.TestObjTypeFactory
import org.rsmod.game.obj.Obj
import org.rsmod.map.CoordGrid

class ObjStackEntryListTest {
    @Test
    fun `stack is first-in-last-out`() {
        val list = ObjStackEntryList()
        check(list.size == 0)

        val first = createObj(id = 995, count = 100, coords = CoordGrid.ZERO)
        val second = createObj(id = 995, count = 250, coords = CoordGrid.ZERO)

        list.add(first)
        assertEquals(1, list.size)
        assertEquals(first, list.findAll(first.coords).first())

        list.add(second)
        assertEquals(2, list.size)
        assertEquals(first, list.findAll(first.coords).first())
        assertEquals(second, list.findAll(first.coords).last())
    }

    @Test
    fun `findAll in specific zone coord grid`() {
        val list = ObjStackEntryList()
        check(list.size == 0)

        val first = createObj(id = 995, count = 100, coords = CoordGrid.ZERO)
        val second = createObj(id = 995, count = 250, coords = CoordGrid.ZERO)
        val third = createObj(id = 995, count = 100, coords = CoordGrid.ZERO.translateX(1))

        list.add(first)
        list.add(second)
        list.add(third)
        check(list.size == 3)

        assertEquals(2, list.findAll(first.coords).count())
        assertTrue(first in list.findAll(first.coords))
        assertTrue(second in list.findAll(first.coords))

        assertEquals(1, list.findAll(third.coords).count())
        assertTrue(third in list.findAll(third.coords))
    }

    private fun createObj(id: Int, count: Int, coords: CoordGrid): Obj {
        val type = TestObjTypeFactory().create(id)
        return TestObjFactory().create(type, count, coords)
    }
}
