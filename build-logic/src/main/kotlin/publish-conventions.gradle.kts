val ossrhUsername: String? by ext
val ossrhPassword: String? by ext

plugins {
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            name = "runerealm"
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
        artifactId = "runerealm-${project.name}"
        pom {
            name.set(project.name)
            description.set(project.description)
            url.set("https://github.com/3ncrypted-816")
            inceptionYear.set("2022")

            organization {
                name.set("RuneRealm")
                url.set("https://github.com/3ncrypted-816")
            }

            licenses {
                license {
                    name.set("ISC License")
                    url.set("https://opensource.org/licenses/isc-license.txt")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/3ncrypted-816/rsmod2.git")
                developerConnection.set("scm:git:git@github.com:3ncrypted-816/rsmod2.git")
                url.set("https://github.com/3ncrypted-816/rsmod2")
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
