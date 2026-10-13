package org.runerealm.api.combat.weapon

import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.obj.Wearpos

internal val Player.righthand: InvObj?
    get() = worn[Wearpos.RightHand.slot]
