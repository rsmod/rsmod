package org.runerealm.api.type.verifier

import org.runerealm.module.ExtendedModule

public object TypeVerifierModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeVerifier>()
    }
}
