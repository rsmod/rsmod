package org.rsmod.plugins.api.net.upstream

import org.rsmod.protocol.game.packet.UpstreamPacket

public open class IfButton(
    public val component: Int,
    public val dynamicChild: Int,
    public val item: Int
) : UpstreamPacket {

    public operator fun component1(): Int = component
    public operator fun component2(): Int = dynamicChild
    public operator fun component3(): Int = item
}

public class IfButton1(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)
public class IfButton2(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)
public class IfButton3(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)
public class IfButton4(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)
public class IfButton5(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)
public class IfButton6(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)
public class IfButton7(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)
public class IfButton8(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)
public class IfButton9(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)
public class IfButton10(component: Int, dynamicChild: Int, item: Int) : IfButton(component, dynamicChild, item)

