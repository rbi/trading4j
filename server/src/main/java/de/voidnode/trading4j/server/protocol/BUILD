java_library (
  name = "protocol",
  srcs = glob([
    "*.java",
    "expertadvisor/*.java"
  ]),
  deps = [
    "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/moneymanagement",
    "//server/src/main/java/de/voidnode/trading4j/server/protocol/messages",
    "//server/src/main/java/de/voidnode/trading4j/server/protocol/exceptions",
    "//server/src/main/java/de/voidnode/trading4j/server/reporting",
  ],
  visibility = [
    "//server/src/main/java/de/voidnode/trading4j/server:__pkg__",
    "//server/src/main/java/de/voidnode/trading4j/server/oio:__pkg__",
  ]
)