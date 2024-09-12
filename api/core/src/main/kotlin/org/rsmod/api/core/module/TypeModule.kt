package org.rsmod.api.core.module

import org.rsmod.api.type.builders.TypeBuilderModule
import org.rsmod.api.type.editors.TypeEditorModule
import org.rsmod.api.type.refs.TypeReferenceModule
import org.rsmod.api.type.resolver.TypeResolverModule
import org.rsmod.api.type.updater.TypeUpdaterModule
import org.rsmod.api.type.verifier.TypeVerifierModule
import org.rsmod.module.ExtendedModule

public object TypeModule : ExtendedModule() {
    override fun bind() {
        install(TypeBuilderModule)
        install(TypeEditorModule)
        install(TypeReferenceModule)
        install(TypeResolverModule)
        install(TypeVerifierModule)
        install(TypeUpdaterModule)
    }
}
