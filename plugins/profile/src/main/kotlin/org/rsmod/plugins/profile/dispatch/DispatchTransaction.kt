package org.rsmod.plugins.profile.dispatch

import java.util.concurrent.atomic.AtomicBoolean

public class DispatchTransaction<L : DispatchRequest, R : DispatchResponse>(
    public val ready: AtomicBoolean = AtomicBoolean(),
    public val request: L
) {

    public lateinit var response: R
}
