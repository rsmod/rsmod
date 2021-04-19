package org.rsmod.plugins.content.combat.special

import org.rsmod.game.event.impl.AccountCreation
import org.rsmod.game.event.impl.ServerStartup

onEvent<ServerStartup>().then { setInternalVarps() }
onEvent<AccountCreation>().then { player.setDefaultSpecialAttackVarps() }

fun setInternalVarps() {
    configureInternalVarps(
        specialEnergy = varp("special_attack_energy"),
        specialState = varp("special_attack_state")
    )
}
