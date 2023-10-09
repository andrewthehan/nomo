load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(
    default_visibility = [
        "//java/dev/andrewhan/nomo/boot:__subpackages__",
        "//java/dev/andrewhan/nomo/examples:__subpackages__",
        "//java/dev/andrewhan/nomo/integrations:__subpackages__",
    ],
)

kt_jvm_library(
    name = "sdk",
    srcs = glob(["**/*.kt"]),
    deps = [
        "//java/dev/andrewhan/nomo/core",
        "@maven//:org_jetbrains_kotlin_kotlin_reflect",
    ],
)
