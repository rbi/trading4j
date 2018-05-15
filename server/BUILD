java_library (
  name = "server",
  srcs = glob([
    "src/main/java/**/*.java",
  ]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/expertadvisorfactory",
    "//core/src/main/java/de/voidnode/trading4j/moneymanagement",
    "//core/src/main/java/de/voidnode/trading4j/moneymanagement/standard",
    "//core/src/main/java/de/voidnode/trading4j/tradetracker",
    "@com_sun_mail_javax_mail//jar",
  ],
  visibility = ["//visibility:public"]
)
