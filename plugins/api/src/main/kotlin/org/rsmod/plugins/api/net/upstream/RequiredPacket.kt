package org.rsmod.plugins.api.net.upstream

import org.rsmod.protocol.game.packet.UpstreamDiscardPacket

public object NoTimeout : UpstreamDiscardPacket
public object ReflectionCheckReply : UpstreamDiscardPacket
public object MapBuildComplete : UpstreamDiscardPacket
