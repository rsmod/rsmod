package org.runerealm.content.interfaces.bank.configs

import org.runerealm.api.type.refs.obj.ObjReferences

internal typealias bank_objs = BankObjs

object BankObjs : ObjReferences() {
    val filler = find("bank_filler", 8792460107028666499)
}
