package org.rsmod.plugins.api.net.upstream

import org.rsmod.protocol.game.packet.UpstreamPacket

public class EventMouseMove : UpstreamPacket
public class EventMouseClick : UpstreamPacket
public class EventMouseIdle : UpstreamPacket
public class EventAppletFocus : UpstreamPacket
public class EventKeyboard : UpstreamPacket
public class EventCameraPosition : UpstreamPacket
public data class WindowStatus(val mode: Int, val width: Int, val height: Int) : UpstreamPacket
