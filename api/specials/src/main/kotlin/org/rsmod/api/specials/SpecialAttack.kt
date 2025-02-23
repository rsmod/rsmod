package org.rsmod.api.specials

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player

public sealed class SpecialAttack {
    public data class Instant(val energyInHundreds: Int, val special: InstantSpecialAttack) :
        SpecialAttack() {
        public suspend fun activate(access: ProtectedAccess): Unit =
            with(special) { access.activate() }
    }

    public data class Combat(val energyInHundreds: Int, val special: CombatSpecialAttack) :
        SpecialAttack() {
        public suspend fun attack(access: ProtectedAccess, target: PathingEntity): Unit =
            with(special) { access.attack(target) }
    }
}

public fun interface InstantSpecialAttack {
    public suspend fun ProtectedAccess.activate()
}

public fun interface CombatSpecialAttack {
    public suspend fun ProtectedAccess.attack(target: PathingEntity)

    public fun interface PlayerSpecific {
        public suspend fun ProtectedAccess.attack(target: Player)
    }

    public fun interface NpcSpecific {
        public suspend fun ProtectedAccess.attack(target: Npc)
    }

    public companion object {
        public fun from(player: PlayerSpecific, npc: NpcSpecific): CombatSpecialAttack =
            CombatSpecialAttack { entity ->
                when (entity) {
                    is Npc -> with(npc) { attack(entity) }
                    is Player -> with(player) { attack(entity) }
                }
            }
    }
}
