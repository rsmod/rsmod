@file:Suppress("UnstableApiUsage")

val catalogs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val junitVersion by lazy { catalogs.findVersion("junit").get().requiredVersion }

plugins {
    `jvm-test-suite`
}

testing.suites {
    val integration by registering(JvmTestSuite::class) {
        testType = TestSuiteType.INTEGRATION_TEST
        useJUnitJupiter(junitVersion)
        targets.all {
            dependencies {
                implementation(project())
                implementation(project(":api:testing"))
            }
            testTask.configure {
                workingDir = rootDir
                systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
                systemProperty("junit.jupiter.execution.parallel.enabled", true)
                systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
                systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "same_thread")
            }
        }
    }
}
