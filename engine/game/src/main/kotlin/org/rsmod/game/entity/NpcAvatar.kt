package org.rsmod.game.entity

import org.rsmod.game.entity.npc.NoopNpcInfo
import org.rsmod.game.entity.npc.NpcInfoProtocol

public class NpcAvatar(size: Int, public var infoProtocol: NpcInfoProtocol = NoopNpcInfo) :
    PathingEntityAvatar(size)
