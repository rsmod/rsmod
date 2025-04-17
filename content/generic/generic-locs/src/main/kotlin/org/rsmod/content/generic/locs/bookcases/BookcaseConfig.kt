package org.rsmod.content.generic.locs.bookcases

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias bookcase_locs = BookcaseLocs

internal object BookcaseLocs : LocReferences() {
    val bookcase = find("bookcase")
    val fai_varrock = find("fai_varrock_posh_bookcase_short_east_offset")
}

internal object BookcaseLocEdits : LocEditor() {
    init {
        edit(bookcase_locs.fai_varrock) { contentGroup = content.bookcase }
        edit(bookcase_locs.bookcase) { contentGroup = content.bookcase }
    }
}
