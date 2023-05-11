package org.rsmod.plugins.api.net.upstream

import org.rsmod.game.protocol.packet.UpstreamDiscardPacket

public object NoTimeout : UpstreamDiscardPacket
public object ReflectionCheckReply : UpstreamDiscardPacket
public object MapBuildComplete : UpstreamDiscardPacket
