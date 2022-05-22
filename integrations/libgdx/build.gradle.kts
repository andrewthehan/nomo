plugins {
    id("dev.andrewhan.nomo.kotlin-library-conventions")
}

val gdxVersion = "1.10.0"
val ktxVersion = "1.10.0-rc1"

dependencies {
    implementation(project(":sdk"))

    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")

    implementation("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")

    implementation("io.github.libktx:ktx-app:$ktxVersion")
    implementation("io.github.libktx:ktx-async:$ktxVersion")
    implementation("io.github.libktx:ktx-box2d:$ktxVersion")
    implementation("io.github.libktx:ktx-graphics:$ktxVersion")
}