package org.rsmod.plugins.api.net.downstream

import org.rsmod.game.protocol.packet.DownstreamPacket
import org.rsmod.plugins.api.model.ui.InterfaceType

public data class IfOpenTop(val interfaceId: Int) : DownstreamPacket
public data class IfOpenSub(val interfaceId: Int, val targetComponent: Int, val type: InterfaceType) : DownstreamPacket
public data class IfCloseSub(val component: Int) : DownstreamPacket
public data class IfSetText(val component: Int, val text: String) : DownstreamPacket
public data class IfSetEvents(val component: Int, val dynamic: IntRange, val events: Int) : DownstreamPacket
public data class IfSetNpcHead(val component: Int, val npc: Int) : DownstreamPacket
public data class IfSetPlayerHead(val component: Int) : DownstreamPacket
public data class IfSetObject(val component: Int, val item: Int, val amountOrZoom: Int) : DownstreamPacket
public data class IfSetAnim(val component: Int, val anim: Int) : DownstreamPacket
