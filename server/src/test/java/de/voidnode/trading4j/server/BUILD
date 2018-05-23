load("//src/build/bazel:javatest.bzl", "gen_java_test_rules")

java_library (
  name = "testhelper",
  srcs = glob(["*.java"], exclude = ["*AT.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//server/src/main/java/de/voidnode/trading4j/server",
    "//server/src/main/java/de/voidnode/trading4j/server/oio",
    "//server/src/main/java/de/voidnode/trading4j/server/protocol/messages",
  ],
)

gen_java_test_rules(
  name = "SystemTests",
  srcs = glob(["*AT.java"]),
  deps = [
    ":testhelper",
    "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/test/java/de/voidnode/trading4j/testutils/assertions",
    "@junit_junit//jar",
    "@org_assertj_assertj_core//jar",
  ]
)