package org.rsmod.api.account.loader.request

public sealed class AccountLoadAuth {
    public sealed class InitialRequest : AccountLoadAuth()

    public data object UnknownDevice : InitialRequest()

    public data class TrustedDevice(val identifier: Int) : InitialRequest()

    public sealed class CodeInput : AccountLoadAuth() {
        public abstract val otp: Int
    }

    public data class AuthCodeInputTrusted(override val otp: Int) : CodeInput()

    public data class AuthCodeInputUntrusted(override val otp: Int) : CodeInput()
}
