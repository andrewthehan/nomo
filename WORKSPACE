load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# START Kotlin
rules_kotlin_version = "1.8"

rules_kotlin_sha = "01293740a16e474669aba5b5a1fe3d368de5832442f164e4fbfc566815a8bc3a"

http_archive(
    name = "rules_kotlin",
    sha256 = rules_kotlin_sha,
    urls = ["https://github.com/bazelbuild/rules_kotlin/releases/download/v%s/rules_kotlin_release.tgz" % rules_kotlin_version],
)

load("@rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")

kotlin_repositories()  # if you want the default. Otherwise see custom kotlinc distribution below

load("@rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")

kt_register_toolchains()  # to use the default toolchain, otherwise see toolchains below
# END Kotlin

# START Maven deps
RULES_JVM_EXTERNAL_TAG = "4.3"

RULES_JVM_EXTERNAL_SHA = "6274687f6fc5783b589f56a2f1ed60de3ce1f99bc4e8f9edef3de43bdf7c6e74"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")

KOTLIN_VERSION = "1.6.21"

KOTLINX_COROUTINES_VERSION = "1.6.1"

GUICE_VERSION = "5.1.0"

GDX_VERSION = "1.10.0"

KTX_VERSION = "1.12.0-rc1"

maven_install(
    artifacts = [
        maven.artifact(
            artifact = "gdx-freetype-platform",
            classifier = "natives-desktop",
            group = "com.badlogicgames.gdx",
            version = GDX_VERSION,
        ),
        maven.artifact(
            artifact = "gdx-platform",
            classifier = "natives-desktop",
            group = "com.badlogicgames.gdx",
            version = GDX_VERSION,
        ),
        maven.artifact(
            artifact = "gdx-box2d-platform",
            classifier = "natives-desktop",
            group = "com.badlogicgames.gdx",
            version = GDX_VERSION,
        ),
        "org.jetbrains.kotlin:kotlin-reflect:%s" % KOTLIN_VERSION,
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:%s" % KOTLINX_COROUTINES_VERSION,
        "com.google.inject:guice:%s" % GUICE_VERSION,
        "com.badlogicgames.gdx:gdx-backend-lwjgl3:%s" % GDX_VERSION,
        "com.badlogicgames.gdx:gdx:%s" % GDX_VERSION,
        "com.badlogicgames.gdx:gdx-box2d:%s" % GDX_VERSION,
        "io.github.libktx:ktx-app:%s" % KTX_VERSION,
        "io.github.libktx:ktx-async:%s" % KTX_VERSION,
        "io.github.libktx:ktx-box2d:%s" % KTX_VERSION,
        "io.github.libktx:ktx-graphics:%s" % KTX_VERSION,
        "io.github.libktx:ktx-math:%s" % KTX_VERSION,
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)
# END Maven deps
