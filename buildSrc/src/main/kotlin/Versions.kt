import org.gradle.api.JavaVersion

object ProjectVersions {
    const val RSMOD = "0.0.1-SNAPSHOT"
    const val PROTOCOL = "186.0-SNAPSHOT"
    const val GUTHIX_BYTEBUF = "0.1.0"
    const val JS5_STORE = "0.4.0"
}

object JvmVersions {
    val JAVA = JavaVersion.VERSION_11
    const val JVM = "11"
    const val KOTLIN = "1.4.10"
    const val COROUTINE = "1.1.0"
    const val KOTLINTER = "3.0.2"
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
    const val JACKSON = "2.11.2"
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
    const val BCRYPT = "0.4"
}
