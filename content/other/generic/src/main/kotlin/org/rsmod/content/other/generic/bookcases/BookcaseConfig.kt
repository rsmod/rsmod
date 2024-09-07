package org.rsmod.content.other.generic.bookcases

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor

internal object BookcaseLocEdits : LocEditor() {
    init {
        edit("nicebookcase_varrock") { contentType = content.bookcase.id }

        edit("bookcase") { contentType = content.bookcase.id }
    }
}
