package org.rsmod.plugins.profile.dispatch.transaction

import org.rsmod.plugins.profile.dispatch.DispatchRequest
import org.rsmod.plugins.profile.dispatch.DispatchResponse
import java.util.concurrent.atomic.AtomicBoolean

public class DispatchTransaction<L : DispatchRequest, R : DispatchResponse>(
    public val ready: AtomicBoolean = AtomicBoolean(),
    public val request: L
) {

    public lateinit var response: R
}
