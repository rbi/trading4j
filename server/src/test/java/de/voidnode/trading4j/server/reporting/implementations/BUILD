load("//src/build/bazel:javatest.bzl", "gen_java_test_rules")

gen_java_test_rules(
  name = "UnitTests",
  srcs = glob(["*Test.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/tradetracker",
    "//server/src/main/java/de/voidnode/trading4j/server/reporting",
    "//server/src/main/java/de/voidnode/trading4j/server/reporting/implementations",
    "@junit_junit//jar",
    "@org_mockito_mockito_all//jar",
  ]
)

gen_java_test_rules(
  name = "IntegrationTests",
  srcs = glob(["*IT.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/test/java/de/voidnode/trading4j/testutils",
    "//server/src/main/java/de/voidnode/trading4j/server/reporting",
    "//server/src/main/java/de/voidnode/trading4j/server/reporting/implementations",
    "@junit_junit//jar",
    "@org_assertj_assertj_core//jar",
    "@org_mockito_mockito_all//jar",
    "@com_bitmechanic_dumbster//jar",
    "@com_sun_mail_javax_mail//jar",
  ]
)