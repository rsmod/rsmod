package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket

data class UpdateRunEnergy(val energy: Int) : ServerPacket

data class UpdateStat(val skill: Int, val currLevel: Int, val xp: Int) : ServerPacket
