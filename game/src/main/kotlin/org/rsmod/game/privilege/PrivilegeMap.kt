package org.rsmod.game.privilege

import org.rsmod.game.name.NamedTypeMap

class PrivilegeMap : NamedTypeMap<Privilege>() {

    fun register(privilege: Privilege) {
        this[privilege.nameId] = privilege
    }
}
