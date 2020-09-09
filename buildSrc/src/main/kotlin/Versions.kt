import org.gradle.api.JavaVersion

object ProjectVersions {
    const val RSMOD_VERSION = "0.0.1-SNAPSHOT"
    const val GUTHIX_BYTEBUF_VERSION = "0.1.0"
}

object JvmVersions {
    const val JVM_VERSION = "11"
    val JAVA_VERSION = JavaVersion.VERSION_11
    const val KOTLIN_VERSION = "1.3.72"
    const val COROUTINE_VERSION = "1.1.0"
}

object LoggerVersions {
    const val SL4J = "1.7.25"
    const val LOG4J = "2.11.2"
    const val INLINE_LOGGER = "1.0.0"
}

object DependencyInjectionVersions {
    const val GUICE = "4.2.2"
    const val KOTLIN_GUICE = "1.4.0"
}

object JacksonVersions {
    const val JACKSON = "2.5.0"
}

object NetVersions {
    const val NETTY = "4.1.51.Final"
}

object TestVersions {
    const val JUNIT = "5.5.1"
}

object ReflectionVersions {
    const val CLASSGRAPH = "4.8.43"
}

object SecurityVersions {
    const val BOUNCYCASTLE = "1.54"
}
