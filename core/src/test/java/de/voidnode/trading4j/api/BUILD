load("//src/build/bazel:javatest.bzl", "gen_java_test_rules")

gen_java_test_rules(
  name = "UnitTests",
  srcs = glob(["*.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/api",
    "@junit_junit//jar",
    "@org_assertj_assertj_core//jar",
  ]
)
