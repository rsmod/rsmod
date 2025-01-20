package org.rsmod.content.other.generic.locs.bookcases

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor

internal object BookcaseLocEdits : LocEditor() {
    init {
        edit("nicebookcase_varrock") { contentGroup = content.bookcase }

        edit("bookcase") { contentGroup = content.bookcase }
    }
}
