package org.runerealm.game.entity

import org.runerealm.game.entity.npc.NoopNpcInfo
import org.runerealm.game.entity.npc.NpcInfoProtocol

public class NpcAvatar(size: Int, public var infoProtocol: NpcInfoProtocol = NoopNpcInfo) :
    PathingEntityAvatar(size)
