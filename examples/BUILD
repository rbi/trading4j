java_binary(
  name = "examples",
  srcs = glob([
    "src/main/java/**/*.java",
  ]),
  main_class = "de.voidnode.trading4j.examples.ExpertAdvisorServer",
  deps = [
   "//core/src/main/java/de/voidnode/trading4j/api",
    "//core/src/main/java/de/voidnode/trading4j/domain",
    "//core/src/main/java/de/voidnode/trading4j/functionality/broker",
    "//core/src/main/java/de/voidnode/trading4j/functionality/expertadvisor",
    "//core/src/main/java/de/voidnode/trading4j/functionality/timeframeconversion",
    "//core/src/main/java/de/voidnode/trading4j/indicators",
    "//core/src/main/java/de/voidnode/trading4j/moneymanagement/standard",
    "//core/src/main/java/de/voidnode/trading4j/strategyexpertadvisor",
    "//server"
  ]
)
