package org.rsmod.api.registry.obj

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.factory.cert1
import org.rsmod.api.testing.factory.cert2
import org.rsmod.api.testing.factory.objFactory
import org.rsmod.api.testing.factory.objRegistryFactory
import org.rsmod.api.testing.factory.objTypeListFactory
import org.rsmod.api.testing.factory.stackable1
import org.rsmod.api.testing.factory.stackable2
import org.rsmod.api.testing.factory.standard1
import org.rsmod.api.testing.factory.standard2
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

class ObjRegistryTest {
    @Test
    fun `delete oldest standard obj added`() {
        val types = objTypeListFactory.createDefault()
        val type = types.standard1()

        val obj1 = objFactory.create(type)
        val obj2 = objFactory.create(type)

        val registry = objRegistryFactory.create()
        check(registry.count() == 0)

        registry.add(obj1)
        registry.add(obj2)
        check(registry.count() == 2)

        val find = registry.findAll(CoordGrid.ZERO).first { it.type == type.id }
        registry.del(find)

        assertEquals(1, registry.count())
        assertEquals(obj2, registry.findAll(CoordGrid.ZERO).single())
    }

    @Test
    fun `add standard obj to empty registry`() {
        val types = objTypeListFactory.createDefault()
        val obj = objFactory.create(types.standard1())

        val registry = objRegistryFactory.create()
        check(registry.count() == 0)

        registry.add(obj)
        assertEquals(1, registry.count())
        assertEquals(obj, registry.findAll(ZoneKey.from(obj.coords)).single())
        assertEquals(obj, registry.findAll(obj.coords).single())
    }

    @Test
    fun `add standard objs to different coordinates in same zone`() {
        val types = objTypeListFactory.createDefault()
        val obj1 = objFactory.create(types.standard1(), coords = CoordGrid.ZERO)
        val obj2 = objFactory.create(types.standard1(), coords = CoordGrid.ZERO.translateX(1))

        val registry = objRegistryFactory.create()
        check(registry.count() == 0)

        registry.add(obj1)
        registry.add(obj2)

        assertEquals(2, registry.count())
        assertEquals(obj1, registry.findAll(obj1.coords).single())
        assertEquals(obj2, registry.findAll(obj2.coords).single())
    }

    @Test
    fun `add private standard objs on same coordinates`() {
        val types = objTypeListFactory.createDefault()
        val obj1 = objFactory.create(types.standard1(), receiverId = 1)
        val obj2 = objFactory.create(types.standard1(), receiverId = 1)

        val registry = objRegistryFactory.create()
        check(registry.count() == 0)

        registry.add(obj1)
        registry.add(obj2)

        assertEquals(2, registry.count())
        assertEquals(setOf(obj1, obj2), registry.findAll(obj1.coords).toSet())
    }

    @Test
    fun `merge private stackable objs on same coordinates`() {
        val types = objTypeListFactory.createDefault()
        val obj1 = objFactory.create(types.stackable1(), count = 1, receiverId = 1)
        val obj2 = objFactory.create(types.stackable1(), count = 1, receiverId = 1)

        val registry = objRegistryFactory.create()
        check(registry.count() == 0)

        val expectedCount = obj1.count + obj2.count
        registry.add(obj1)
        registry.add(obj2)

        assertEquals(1, registry.count())
        assertEquals(expectedCount, obj1.count)
    }

    @Test
    fun `add differing private stackable objs on same coordinates`() {
        val types = objTypeListFactory.createDefault()
        val obj1 = objFactory.create(types.stackable1(), receiverId = 1)
        val obj2 = objFactory.create(types.stackable2(), receiverId = 1)

        val registry = objRegistryFactory.create()
        check(registry.count() == 0)

        registry.add(obj1)
        registry.add(obj2)

        assertEquals(2, registry.count())
        assertEquals(setOf(obj1, obj2), registry.findAll(obj1.coords).toSet())
    }

    @Test
    fun `split private stackable objs on same coordinates if count overflows`() {
        val types = objTypeListFactory.createDefault()
        val obj1 = objFactory.create(types.stackable1(), receiverId = 1)
        val obj2 =
            objFactory.create(
                type = types.stackable1(),
                count = Int.MAX_VALUE,
                coords = CoordGrid.ZERO,
                receiverId = 1,
            )

        val registry = objRegistryFactory.create()
        check(registry.count() == 0)

        registry.add(obj1)
        registry.add(obj2)

        assertEquals(2, registry.count())
        assertEquals(setOf(obj1, obj2), registry.findAll(obj1.coords).toSet())
    }

    @TestWithArgs(ObjPairProvider::class)
    fun `add public objs on same coordinates`(type1: UnpackedObjType, type2: UnpackedObjType) {
        val obj1 = objFactory.create(type1)
        val obj2 = objFactory.create(type2)

        val registry = objRegistryFactory.create()
        check(registry.count() == 0)

        registry.add(obj1)
        registry.add(obj2)

        assertEquals(2, registry.count())
        assertEquals(setOf(obj1, obj2), registry.findAll(obj1.coords).toSet())
    }

    private object ObjPairProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            val types = objTypeListFactory.createDefault()
            return listOf(
                TestArgs(types.standard1(), types.standard1()),
                TestArgs(types.standard1(), types.standard2()),
                TestArgs(types.standard1(), types.stackable1()),
                TestArgs(types.standard1(), types.stackable2()),
                TestArgs(types.standard1(), types.cert1()),
                TestArgs(types.standard1(), types.cert2()),
                TestArgs(types.standard2(), types.standard1()),
                TestArgs(types.standard2(), types.standard2()),
                TestArgs(types.standard2(), types.stackable1()),
                TestArgs(types.standard2(), types.stackable2()),
                TestArgs(types.standard2(), types.cert1()),
                TestArgs(types.standard2(), types.cert2()),
                TestArgs(types.stackable1(), types.standard1()),
                TestArgs(types.stackable1(), types.standard2()),
                TestArgs(types.stackable1(), types.stackable1()),
                TestArgs(types.stackable1(), types.stackable2()),
                TestArgs(types.stackable1(), types.cert1()),
                TestArgs(types.stackable1(), types.cert2()),
                TestArgs(types.stackable2(), types.standard1()),
                TestArgs(types.stackable2(), types.standard2()),
                TestArgs(types.stackable2(), types.stackable1()),
                TestArgs(types.stackable2(), types.stackable2()),
                TestArgs(types.stackable2(), types.cert1()),
                TestArgs(types.stackable2(), types.cert2()),
                TestArgs(types.cert1(), types.standard1()),
                TestArgs(types.cert1(), types.standard2()),
                TestArgs(types.cert1(), types.stackable1()),
                TestArgs(types.cert1(), types.stackable2()),
                TestArgs(types.cert1(), types.cert1()),
                TestArgs(types.cert1(), types.cert2()),
                TestArgs(types.cert2(), types.standard1()),
                TestArgs(types.cert2(), types.standard2()),
                TestArgs(types.cert2(), types.stackable1()),
                TestArgs(types.cert2(), types.stackable2()),
                TestArgs(types.cert2(), types.cert1()),
                TestArgs(types.cert2(), types.cert2()),
            )
        }
    }
}
