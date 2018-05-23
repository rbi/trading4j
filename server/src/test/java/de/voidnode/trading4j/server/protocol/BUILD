load("//src/build/bazel:javatest.bzl", "gen_java_test_rules")

gen_java_test_rules(
  name = "UnitTests",
  srcs = glob(["*.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/moneymanagement",
    "//core/src/test/java/de/voidnode/trading4j/testutils",
    "//core/src/test/java/de/voidnode/trading4j/testutils/assertions",
    "//server/src/main/java/de/voidnode/trading4j/server/reporting",
    "//server/src/main/java/de/voidnode/trading4j/server/protocol",
    "//server/src/main/java/de/voidnode/trading4j/server/protocol/exceptions",
    "//server/src/main/java/de/voidnode/trading4j/server/protocol/messages",
    "@junit_junit//jar",
    "@org_mockito_mockito_all//jar",
    "@org_assertj_assertj_core//jar",
  ]
)
