load("//src/build/bazel:javatest.bzl", "gen_java_test_rules")

gen_java_test_rules(
  name = "UnitTests",
  srcs = glob(["*.java"]),
  deps = [
    "//server/src/main/java/de/voidnode/trading4j/server/protocol/messages",
    "@junit_junit//jar",
    "@org_assertj_assertj_core//jar",
  ]
)
