package org.rsmod.content.generic.locs.bookcases

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor

internal object BookcaseLocEdits : LocEditor() {
    init {
        edit("fai_varrock_posh_bookcase_short_east_offset") { contentGroup = content.bookcase }

        edit("bookcase") { contentGroup = content.bookcase }
    }
}
