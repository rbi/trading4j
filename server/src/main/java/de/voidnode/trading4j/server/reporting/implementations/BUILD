java_library (
  name = "implementations",
  srcs = glob(["*.java"]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/tradetracker",
    "//server/src/main/java/de/voidnode/trading4j/server/reporting",
    "@com_sun_mail_javax_mail//jar",
  ],
  visibility = [
    "//server/src/main/java/de/voidnode/trading4j/server:__pkg__",
  ]
)