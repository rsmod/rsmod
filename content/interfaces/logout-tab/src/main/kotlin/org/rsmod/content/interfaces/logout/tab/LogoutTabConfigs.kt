package org.rsmod.content.interfaces.logout.tab

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias logout_components = LogoutTabComponent

object LogoutTabComponent : ComponentReferences() {
    val logout = find("logout:logout")
}
