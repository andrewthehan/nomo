plugins { id("dev.andrewhan.nomo.kotlin-application-conventions") }

val gdxVersion = "1.10.0"
val ktxVersion = "1.10.0-rc1"

dependencies {
  implementation(project(":boot"))
  implementation(project(":core"))
  implementation(project(":integrations:libgdx"))
  implementation(project(":sdk"))

  implementation("javax.inject:javax.inject:1")

  implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
  implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
  implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
  implementation("io.github.libktx:ktx-app:$ktxVersion")
  implementation("io.github.libktx:ktx-graphics:$ktxVersion")
}

application { mainClass.set("dev.andrewhan.nomo.example.AppKt") }
