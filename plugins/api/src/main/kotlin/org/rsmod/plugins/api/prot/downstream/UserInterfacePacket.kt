package org.rsmod.plugins.api.prot.downstream

import org.rsmod.protocol.packet.DownstreamPacket

data class IfOpenTop(val interfaceId: Int) : DownstreamPacket
data class IfOpenSub(val interfaceId: Int, val targetComponent: Int, val clickMode: Int) : DownstreamPacket
data class IfCloseSub(val component: Int) : DownstreamPacket
data class IfSetText(val component: Int, val text: String) : DownstreamPacket
data class IfSetEvents(val component: Int, val dynamic: IntRange, val events: Int) : DownstreamPacket
data class IfSetNpcHead(val component: Int, val npc: Int) : DownstreamPacket
data class IfSetPlayerHead(val component: Int) : DownstreamPacket
data class IfSetObject(val component: Int, val item: Int, val amountOrZoom: Int) : DownstreamPacket
data class IfSetAnim(val component: Int, val anim: Int) : DownstreamPacket
