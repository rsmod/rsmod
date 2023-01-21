package org.rsmod.plugins.net.game.upstream

import org.rsmod.plugins.net.rev.builder.upstream.UpstreamPacket

class EventMouseMove : UpstreamPacket
class EventMouseClick : UpstreamPacket
class EventMouseIdle : UpstreamPacket
class EventAppletFocus : UpstreamPacket
class EventKeyboard : UpstreamPacket
class EventCameraPosition : UpstreamPacket
class WindowStatus(val mode: Int, val width: Int, val height: Int) : UpstreamPacket
