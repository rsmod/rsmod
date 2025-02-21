@file:Suppress("unused")

package org.rsmod.content.interfaces.gameframe.config

import org.rsmod.api.config.aliases.EnumComp
import org.rsmod.api.type.refs.enums.EnumReferences

object GameframeEnums : EnumReferences() {
    val fixed_pane_redirect = find<EnumComp, EnumComp>("fixed_pane_redirect", 4205535)
    val resizable_basic_pane_redirect =
        find<EnumComp, EnumComp>("resizable_basic_pane_redirect", 4209256)
    val side_panels_resizable_pane_redirect =
        find<EnumComp, EnumComp>("side_panels_resizable_pane_redirect", 4212977)
}
