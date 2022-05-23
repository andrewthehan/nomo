plugins {
    id("dev.andrewhan.nomo.kotlin-library-conventions")
}

dependencies {
    api(project(":core"))
    api(project(":sdk"))

    implementation("javax.inject:javax.inject:1")
}
