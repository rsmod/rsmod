package org.rsmod.api.testing.params

/** [TestArgs] represents an array of objects to be used for invoking a [TestWithArgs] method. */
public class TestArgs(public val first: Any?, public vararg val rest: Any?)
