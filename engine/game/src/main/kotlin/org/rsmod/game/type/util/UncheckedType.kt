package org.rsmod.game.type.util

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CLASS,
    AnnotationTarget.CONSTRUCTOR,
)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This declaration bypasses type-safety features. Use only when necessary.",
)
// NOTE: This annotation is defined within the `game` module to ensure that call-site opt-in
// requirements are enforced across modules. If this annotation were placed in a different module
// that isn't a direct dependency of the calling module, the opt-in mechanism would not work, and
// the restrictions on the annotated API would be bypassed, implicitly.
public annotation class UncheckedType(val message: String)
