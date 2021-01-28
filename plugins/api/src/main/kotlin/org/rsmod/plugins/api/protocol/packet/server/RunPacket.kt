package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket

inline class UpdateRunEnergy(val energy: Int) : ServerPacket