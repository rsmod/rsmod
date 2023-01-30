package org.rsmod.game.cache

import javax.inject.Qualifier

/**
 * Represents the [java.nio.file.Path] that leads to the packed cache
 * directory (_main_file_cache._* files).
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
public annotation class CachePath
