load("@rules_java//java:defs.bzl", "java_binary")
load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

java_binary(
    name = "breakout_bin",
    main_class = "dev.andrewhan.nomo.examples.breakout.AppKt",
    runtime_deps = [
        ":breakout",
    ],
)

kt_jvm_library(
    name = "breakout",
    srcs = glob(["**/*.kt"]),
    deps = [
        "//java/dev/andrewhan/nomo/boot/combat",
        "//java/dev/andrewhan/nomo/boot/player",
        "//java/dev/andrewhan/nomo/boot/time",
        "//java/dev/andrewhan/nomo/boot/xp",
        "//java/dev/andrewhan/nomo/core",
        "//java/dev/andrewhan/nomo/integrations/libgdx",
        "//java/dev/andrewhan/nomo/sdk",
        "@maven//:com_badlogicgames_gdx_gdx",
        "@maven//:com_badlogicgames_gdx_gdx_backend_lwjgl3",
        "@maven//:com_badlogicgames_gdx_gdx_box2d",
        "@maven//:com_badlogicgames_gdx_gdx_box2d_platform_natives_desktop",
        "@maven//:com_badlogicgames_gdx_gdx_freetype_platform_natives_desktop",
        "@maven//:com_badlogicgames_gdx_gdx_platform_natives_desktop",
        "@maven//:io_github_libktx_ktx_app",
        "@maven//:io_github_libktx_ktx_async",
        "@maven//:io_github_libktx_ktx_box2d",
        "@maven//:io_github_libktx_ktx_graphics",
        "@maven//:io_github_libktx_ktx_math",
    ],
)
