package org.rsmod.api.testing.factory.obj

import org.rsmod.game.type.obj.ObjTypeList

public class TestObjTypeListFactory {
    public fun createDefault(): ObjTypeList {
        val factory = TestObjTypeFactory()
        val standard1 = factory.create(STANDARD_OBJ_1_ID) {}
        val standard2 = factory.create(STANDARD_OBJ_2_ID) {}
        val stack1 = factory.create(STACKABLE_OBJ_1_ID) { stackable = true }
        val stack2 = factory.create(STACKABLE_OBJ_2_ID) { stackable = true }
        val cert1 = factory.create(CERT_OBJ_1_ID) { certtemplate = 799 }
        val cert2 = factory.create(CERT_OBJ_2_ID) { certtemplate = 799 }
        val map =
            mutableMapOf(
                STANDARD_OBJ_1_ID to standard1,
                STANDARD_OBJ_2_ID to standard2,
                STACKABLE_OBJ_1_ID to stack1,
                STACKABLE_OBJ_2_ID to stack2,
                CERT_OBJ_1_ID to cert1,
                CERT_OBJ_2_ID to cert2,
            )
        return ObjTypeList(map)
    }

    internal companion object {
        const val STANDARD_OBJ_1_ID: Int = 1
        const val STANDARD_OBJ_2_ID: Int = 2
        const val CERT_OBJ_1_ID: Int = 3
        const val CERT_OBJ_2_ID: Int = 4
        const val STACKABLE_OBJ_1_ID: Int = 5
        const val STACKABLE_OBJ_2_ID: Int = 6
    }
}
