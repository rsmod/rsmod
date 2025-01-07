package org.rsmod.api.testing.scope

import jakarta.inject.Inject
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap

/**
 * Holds references to instances that must be treated as read-only during testing.
 *
 * The fields within this class represent components that are shared across multiple tests.
 * Modifying these objects can introduce race conditions, data corruption, and unpredictable test
 * failures, as they are not designed to be thread-safe under modification during test execution.
 *
 * **Important**: Access to these objects is provided for observation and validation purposes only.
 * Do not perform any mutation operations on these objects. Violating this guideline can severely
 * impact the reliability and accuracy of the integration test suite.
 */
public data class AdvancedReadOnly
@Inject
constructor(
    public val zoneUpdateMap: ZoneUpdateMap,
    public val locRegistry: LocRegistry,
    public val objRegistry: ObjRegistry,
)
