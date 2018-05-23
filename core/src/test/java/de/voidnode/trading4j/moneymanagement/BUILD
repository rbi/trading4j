load("//src/build/bazel:javatest.bzl", "gen_java_test_rules")

gen_java_test_rules(
  name = "UnitTests",
  srcs = glob(["*Test.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/moneymanagement",
    "@junit_junit//jar",
    "@org_assertj_assertj_core//jar"
  ]
)

gen_java_test_rules(
  name = "IntegrationTests",
  srcs = glob(["*IT.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/moneymanagement",
    "//core/src/test/java/de/voidnode/trading4j/testutils",
    "@junit_junit//jar",
  ]
)