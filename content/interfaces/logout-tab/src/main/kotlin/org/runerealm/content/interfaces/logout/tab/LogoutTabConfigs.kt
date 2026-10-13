package org.runerealm.content.interfaces.logout.tab

import org.runerealm.api.type.refs.comp.ComponentReferences

typealias logout_components = LogoutTabComponent

object LogoutTabComponent : ComponentReferences() {
    val logout = find("logout:logout")
}
