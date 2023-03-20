package org.rsmod.game.model.mob.info

public class ExtendedInfoMap(
    private val extendedInfo: HashMap<Class<out ExtendedInfo>, ExtendedInfo> = HashMap(),
    private val extendedInfoTypes: ExtendedInfoTypeSet = ExtendedInfoTypeSet()
) {

    public val pendingInfo: Map<Class<out ExtendedInfo>, ExtendedInfo> get() = extendedInfo
    public val pendingTypes: ExtendedInfoTypeSet get() = extendedInfoTypes

    public fun clear() {
        extendedInfo.clear()
        extendedInfoTypes.clear()
    }

    public fun isEmpty(): Boolean {
        return extendedInfo.isEmpty() && extendedInfoTypes.isEmpty()
    }

    public operator fun <T : ExtendedInfo> plusAssign(dynamicType: Class<out T>) {
        extendedInfoTypes += dynamicType
    }

    public operator fun <T : ExtendedInfo> plusAssign(info: T) {
        val type = info.javaClass
        extendedInfo[type] = info
        extendedInfoTypes += type
    }
}
