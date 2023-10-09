load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(
    default_visibility = [
        "//java/dev/andrewhan/nomo/examples:__subpackages__",
    ],
)

kt_jvm_library(
    name = "player",
    srcs = glob(["**/*.kt"]),
    deps = [
        "//java/dev/andrewhan/nomo/core",
        "//java/dev/andrewhan/nomo/sdk",
    ],
)
