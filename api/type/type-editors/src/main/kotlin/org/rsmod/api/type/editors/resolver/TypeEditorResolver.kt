package org.rsmod.api.type.editors.resolver

import org.rsmod.api.type.editors.TypeEditor

public fun interface TypeEditorResolver<B, T> {
    public fun resolve(editors: TypeEditor<B, T>): List<TypeEditorResult>
}
