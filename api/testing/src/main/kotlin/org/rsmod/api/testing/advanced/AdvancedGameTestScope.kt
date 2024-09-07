package org.rsmod.api.testing.advanced

/**
 * Provides a higher-privilege testing scope for integration tests.
 *
 * This scope grants access to additional resources and capabilities that are crucial for certain
 * tests, offering more flexibility and control over the testing environment.
 *
 * It includes, but is not limited to, a reference to [AdvancedReadOnly], which contains objects
 * that must be _treated_ as read-only to avoid race conditions and potential test failures.
 *
 * **Caution**: Using `AdvancedGameTestScope` implies greater responsibility. Consumers are expected
 * to adhere strictly to the guidelines, such as treating certain objects as read-only, to ensure
 * the integrity and stability of the tests. While adherence cannot be enforced, failure to comply
 * can lead to unreliable test outcomes.
 */
public data class AdvancedGameTestScope(public val readOnly: AdvancedReadOnly)
