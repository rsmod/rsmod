val ossrhUsername: String? by ext
val ossrhPassword: String? by ext

plugins {
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            name = "rsmod"
            if (version.toString().endsWith("-SNAPSHOT")) {
                setUrl("https://oss.sonatype.org/content/repositories/snapshots")
            } else {
                setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            }
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    publications.withType<MavenPublication> {
        val targetName = this@withType.name

        artifact(tasks.register("${targetName}JavadocJar", Jar::class) {
            group = LifecycleBasePlugin.BUILD_GROUP
            description = "Assembles a jar archive containing the Javadoc API documentation of target '$targetName'."
            archiveClassifier.set("javadoc")
            archiveAppendix.set(targetName)
        })

        pom {
            name.set(project.name)
            description.set(project.description)
            url.set("https://github.com/rsmod")
            inceptionYear.set("2022")

            organization {
                name.set("RS Mod")
                url.set("https://github.com/rsmod")
            }

            licenses {
                license {
                    name.set("ISC License")
                    url.set("https://opensource.org/licenses/isc-license.txt")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/rsmod/rsmod.git")
                developerConnection.set("scm:git:git@github.com:github.com/rsmod/rsmod.git")
                url.set("https://github.com/rsmod/rsmod")
            }

            developers {
                developer {
                    name.set("Tomm")
                    url.set("https://github.com/Tomm0017")
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}
