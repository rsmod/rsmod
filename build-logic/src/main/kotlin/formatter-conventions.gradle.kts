plugins {
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        ktfmt().kotlinlangStyle().configure {
            it.setMaxWidth(100)
        }
    }
}
