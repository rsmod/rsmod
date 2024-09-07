@file:Suppress("UnstableApiUsage")

val catalogs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val junitVersion by lazy { catalogs.findVersion("junit").get().requiredVersion }

plugins {
    `jvm-test-suite`
}

testing.suites {
    val docTest by registering(JvmTestSuite::class) {
        testType.set("documentation-unit-test")
        useJUnitJupiter(junitVersion)
        targets.all {
            dependencies {
                implementation(project())
            }
            testTask.configure {
                workingDir = rootDir
                systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
                systemProperty("junit.jupiter.execution.parallel.enabled", true)
                systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
                systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
            }
        }
    }

    val konsistTest by registering(JvmTestSuite::class) {
        testType.set("konsist-unit-test")
        useJUnitJupiter(junitVersion)
        targets.all {
            testTask.configure {
                workingDir = rootDir
                systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
                systemProperty("junit.jupiter.execution.parallel.enabled", true)
                systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
                systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
            }
        }
    }
}
