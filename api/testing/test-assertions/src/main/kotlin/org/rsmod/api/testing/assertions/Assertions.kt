package org.rsmod.api.testing.assertions

import kotlin.contracts.contract
import org.junit.jupiter.api.Assertions

public fun assertTrueContract(condition: Boolean) {
    contract { returns() implies condition }
    Assertions.assertTrue(condition)
}

public fun assertFalseContract(condition: Boolean) {
    contract { returns() implies !condition }
    Assertions.assertFalse(condition)
}

public fun assertNullContract(actual: Any?) {
    contract { returns() implies (actual == null) }
    Assertions.assertNull(actual)
}

public fun assertNotNullContract(actual: Any?) {
    contract { returns() implies (actual != null) }
    Assertions.assertNotNull(actual)
}

public fun assertNotNullContract(actual: Any?, messageSupplier: () -> String) {
    contract { returns() implies (actual != null) }
    Assertions.assertNotNull(actual, messageSupplier)
}
