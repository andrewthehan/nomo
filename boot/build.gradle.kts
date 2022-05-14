plugins {
    id("dev.andrewhan.nomo.kotlin-library-conventions")
}

dependencies {
    api(project(":core"))
    api(project(":sdk"))
    api(project(":math"))

    implementation("javax.inject:javax.inject:1")
}
