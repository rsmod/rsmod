package org.rsmod.plugins.content.combat.special

import org.rsmod.game.event.impl.AccountCreation
import org.rsmod.game.event.impl.ServerStartup
import org.rsmod.game.model.vars.type.VarpTypeList

val varps: VarpTypeList by inject()

onEvent<ServerStartup>().then { varps.configureInternalVarps() }
onEvent<AccountCreation>().then { player.setDefaultSpecialAttackVarps() }
