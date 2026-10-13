package org.runerealm.api.type.editors.resolver

import org.runerealm.api.type.editors.TypeEditor

public fun interface TypeEditorResolver<T> {
    public fun resolve(editors: TypeEditor<T>): List<TypeEditorResult>
}
