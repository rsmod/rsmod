package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.api.net.upstream.IfButton
import org.rsmod.plugins.api.net.upstream.IfButton1
import org.rsmod.plugins.api.net.upstream.IfButton10
import org.rsmod.plugins.api.net.upstream.IfButton2
import org.rsmod.plugins.api.net.upstream.IfButton3
import org.rsmod.plugins.api.net.upstream.IfButton4
import org.rsmod.plugins.api.net.upstream.IfButton5
import org.rsmod.plugins.api.net.upstream.IfButton6
import org.rsmod.plugins.api.net.upstream.IfButton7
import org.rsmod.plugins.api.net.upstream.IfButton8
import org.rsmod.plugins.api.net.upstream.IfButton9
import org.rsmod.plugins.api.publish
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedItem

public sealed class IfButtonHandler<T : IfButton>(
    type: Class<T>,
    private val clickType: Int
) : UpstreamHandler<T>(type) {

    override fun handle(player: Player, packet: T) {
        val component = NamedComponent(packet.component)
        val dynamicChild = packet.dynamicChild
        val item = packet.item
        val event = UpstreamEvent.IfButton(
            clickType = clickType,
            component = component,
            dynamicChild = if (dynamicChild != 0xFFFF) dynamicChild else null,
            item = if (item != 0xFFFF) NamedItem(item) else null
        )
        player.publish(component.packed, event)
    }

    public companion object {

        public const val IF_BUTTON1_CLICK_TYPE: Int = 0
        public const val IF_BUTTON2_CLICK_TYPE: Int = 1
        public const val IF_BUTTON3_CLICK_TYPE: Int = 2
        public const val IF_BUTTON4_CLICK_TYPE: Int = 3
        public const val IF_BUTTON5_CLICK_TYPE: Int = 4
        public const val IF_BUTTON6_CLICK_TYPE: Int = 5
        public const val IF_BUTTON7_CLICK_TYPE: Int = 6
        public const val IF_BUTTON8_CLICK_TYPE: Int = 7
        public const val IF_BUTTON9_CLICK_TYPE: Int = 8
        public const val IF_BUTTON10_CLICK_TYPE: Int = 9
    }
}

public class IfButton1Handler : IfButtonHandler<IfButton1>(IfButton1::class.java, IF_BUTTON1_CLICK_TYPE)
public class IfButton2Handler : IfButtonHandler<IfButton2>(IfButton2::class.java, IF_BUTTON2_CLICK_TYPE)
public class IfButton3Handler : IfButtonHandler<IfButton3>(IfButton3::class.java, IF_BUTTON3_CLICK_TYPE)
public class IfButton4Handler : IfButtonHandler<IfButton4>(IfButton4::class.java, IF_BUTTON4_CLICK_TYPE)
public class IfButton5Handler : IfButtonHandler<IfButton5>(IfButton5::class.java, IF_BUTTON5_CLICK_TYPE)
public class IfButton6Handler : IfButtonHandler<IfButton6>(IfButton6::class.java, IF_BUTTON6_CLICK_TYPE)
public class IfButton7Handler : IfButtonHandler<IfButton7>(IfButton7::class.java, IF_BUTTON7_CLICK_TYPE)
public class IfButton8Handler : IfButtonHandler<IfButton8>(IfButton8::class.java, IF_BUTTON8_CLICK_TYPE)
public class IfButton9Handler : IfButtonHandler<IfButton9>(IfButton9::class.java, IF_BUTTON9_CLICK_TYPE)
public class IfButton10Handler : IfButtonHandler<IfButton10>(IfButton10::class.java, IF_BUTTON10_CLICK_TYPE)
