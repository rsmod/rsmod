package gg.rsmod.plugins.api.model.ui

sealed class InterfaceClickMode {
    object Disabled : InterfaceClickMode()
    object Enabled : InterfaceClickMode()
}

enum class InterfaceEvent(internal val bit: Int) {
    PAUSE(0),
    BUTTON1(1),
    BUTTON2(2),
    BUTTON3(3),
    BUTTON4(4),
    BUTTON5(5),
    BUTTON6(6),
    BUTTON7(7),
    BUTTON8(8),
    BUTTON9(9),
    BUTTON10(10),
    OPOBJT(11),
    OPNPCT(12),
    OPLOCT(13),
    OPPLAYERT(14),
    OPHELDT(15),
    BUTTONT(16),
    DRAG_DEPTH1(17),
    DRAG_DEPTH2(18),
    DRAG_DEPTH3(19),
    DRAG_TARGET(20),
    TARGET(21);

    val flag: Int
        get() = bit shl 1

    companion object {

        val values = enumValues<InterfaceEvent>()

        internal val ascending = values.sortedBy { it.bit }
    }
}

operator fun InterfaceEvent.rangeTo(inclusive: InterfaceEvent): Set<InterfaceEvent> {
    val values = InterfaceEvent.ascending
    val bitRange = bit..inclusive.bit
    val filtered = values.filter { it.bit in bitRange }
    return filtered.toSet()
}
