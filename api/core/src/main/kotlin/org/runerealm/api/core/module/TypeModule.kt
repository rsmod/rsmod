package org.runerealm.api.core.module

import org.runerealm.api.type.builders.TypeBuilderModule
import org.runerealm.api.type.editors.TypeEditorModule
import org.runerealm.api.type.refs.TypeReferenceModule
import org.runerealm.api.type.resolver.TypeResolverModule
import org.runerealm.api.type.updater.TypeUpdaterModule
import org.runerealm.api.type.verifier.TypeVerifierModule
import org.runerealm.module.ExtendedModule

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
