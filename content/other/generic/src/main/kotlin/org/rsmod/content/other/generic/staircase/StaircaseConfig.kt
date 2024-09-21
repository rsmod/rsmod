package org.rsmod.content.other.generic.staircase

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor

internal object StaircaseLocEdits : LocEditor() {
    init {
        edit("spiralstaircase_down") { contentGroup = content.spiralstaircase_down }

        edit("spiralstaircase_up") { contentGroup = content.spiralstaircase_up }

        edit("spiralstaircase_option") { contentGroup = content.spiralstaircase_option }
    }
}
