package org.rsmod.api.combat.weapon

import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.Wearpos

internal val Player.righthand: InvObj?
    get() = worn[Wearpos.RightHand.slot]
