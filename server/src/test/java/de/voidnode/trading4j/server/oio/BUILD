load("//src/build/bazel:javatest.bzl", "gen_java_test_rules")

gen_java_test_rules(
  name = "IntegrationTests",
  srcs = glob(["*IT.java"]),
  deps = [
    "//server/src/main/java/de/voidnode/trading4j/server/oio",
    "//server/src/main/java/de/voidnode/trading4j/server/protocol/exceptions",
    "@junit_junit//jar",
    "@org_assertj_assertj_core//jar",
  ]
)