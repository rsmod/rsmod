package org.rsmod.api.type.verifier

import org.rsmod.module.ExtendedModule

public object TypeVerifierModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeVerifier>()
    }
}
