package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias bank_objs = BankObjs

object BankObjs : ObjReferences() {
    val filler = find("bank_filler", 8792460107028666499)
}
