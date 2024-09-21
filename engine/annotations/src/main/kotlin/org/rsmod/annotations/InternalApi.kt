package org.rsmod.annotations

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CLASS,
    AnnotationTarget.CONSTRUCTOR,
)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This feature is internal and should be used cautiously.",
)
public annotation class InternalApi(val message: String)
