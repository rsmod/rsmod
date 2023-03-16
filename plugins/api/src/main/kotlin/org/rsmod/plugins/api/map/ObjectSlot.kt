package org.rsmod.plugins.api.map

public enum class ObjectSlot(public val id: Int) {

    Wall(id = 0),
    Decor(id = 1),
    Main(id = 2),
    GroundDetail(id = 3);

    public companion object {

        public val values: Array<ObjectSlot> = enumValues()
    }
}
