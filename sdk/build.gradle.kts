plugins {
    id("dev.andrewhan.nomo.kotlin-library-conventions")
}

val guiceVersion = "5.1.0"

dependencies {
    api(project(":core"))

    api("com.google.inject:guice:$guiceVersion")
}
