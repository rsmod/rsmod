import org.gradle.api.JavaVersion

object ProjectVersions {
    /**
     * Mix of rs mod API version and rs protocol revision.
     *
     * First  number: RS Mod - MAJOR version
     * Second number: RS Protocol - MAJOR version
     * Third  number: RS Mod - PATCH & MINOR version
     */
    const val RS_MOD_API = "0.192.0-SNAPSHOT"
    const val RS_MOD = "0.0.1-SNAPSHOT"
    const val GUTHIX_BYTEBUF = "0.1.1"
    const val JS5_STORE = "0.4.0"
    const val KOTLIN_RETRY = "1.0.6"
}

object JvmVersions {
    val JAVA = JavaVersion.VERSION_11
    const val JVM = "11"
    const val KOTLIN = "1.4.0"
    const val COROUTINE = "1.4.0"
    const val KOTLINTER = "3.2.0"
}

object LoggerVersions {
    const val SL4J = "1.7.30"
    const val LOG4J = "2.13.3"
    const val INLINE_LOGGER = "1.0.0"
}

object DependencyInjectionVersions {
    const val GUICE = "5.0.0-BETA-1"
    const val KOTLIN_GUICE = "1.4.1"
}

object JacksonVersions {
    const val JACKSON = "2.11.3"
}

object NetVersions {
    const val NETTY = "4.1.53.Final"
}

object TestVersions {
    const val JUNIT = "5.7.0"
}

object ReflectionVersions {
    const val CLASSGRAPH = "4.8.43"
}

object SecurityVersions {
    const val BOUNCYCASTLE = "1.66"
    const val BCRYPT = "0.4"
}
