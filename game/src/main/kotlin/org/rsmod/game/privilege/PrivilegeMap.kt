package org.rsmod.game.privilege

import org.rsmod.game.name.TypeNamedMap

class PrivilegeMap : TypeNamedMap<Privilege>() {

    fun register(privilege: Privilege) {
        this[privilege.nameId] = privilege
    }
}
