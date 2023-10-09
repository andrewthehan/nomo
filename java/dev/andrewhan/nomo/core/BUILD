load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(
    default_visibility = [
        "//java/dev/andrewhan/nomo/boot:__subpackages__",
        "//java/dev/andrewhan/nomo/examples:__subpackages__",
        "//java/dev/andrewhan/nomo/integrations:__subpackages__",
        "//java/dev/andrewhan/nomo/sdk:__subpackages__",
    ],
)

kt_jvm_library(
    name = "core",
    srcs = glob(["*.kt"]),
    deps = [
        "@maven//:com_google_inject_guice",
        "@maven//:org_jetbrains_kotlinx_kotlinx_coroutines_core",
    ],
)
