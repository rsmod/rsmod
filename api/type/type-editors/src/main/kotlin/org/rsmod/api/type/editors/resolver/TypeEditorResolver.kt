package org.rsmod.api.type.editors.resolver

import org.rsmod.api.type.editors.TypeEditor

public fun interface TypeEditorResolver<T> {
    public fun resolve(editors: TypeEditor<T>): List<TypeEditorResult>
}
