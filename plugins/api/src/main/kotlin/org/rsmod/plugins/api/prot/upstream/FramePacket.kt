package org.rsmod.plugins.api.prot.upstream

import org.rsmod.protocol.packet.UpstreamPacket

class EventMouseMove : UpstreamPacket
class EventMouseClick : UpstreamPacket
class EventMouseIdle : UpstreamPacket
class EventAppletFocus : UpstreamPacket
class EventKeyboard : UpstreamPacket
class EventCameraPosition : UpstreamPacket
class WindowStatus(val mode: Int, val width: Int, val height: Int) : UpstreamPacket
