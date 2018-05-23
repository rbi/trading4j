load("//src/build/bazel:javatest.bzl", "gen_java_test_rules")

gen_java_test_rules(
  name = "UnitTests",
  srcs = glob(["*Test.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/tradetracker",
    "@junit_junit//jar",
    "@org_assertj_assertj_core//jar",
    "@org_mockito_mockito_all//jar",
  ]
)

gen_java_test_rules(
  name = "IntegrationTests",
  srcs = glob(["*IT.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/tradetracker",
    "@junit_junit//jar",
    "@org_assertj_assertj_core//jar",
  ],
  resources = [
    "//core/src/test/resources"
  ]
)