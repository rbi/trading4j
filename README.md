# Trading4j
Trading4j is a library that allows to write automated expert advisors for Forex trading. The library promotes writing code that is reusable across different expert advisors by clearly separating between different aspects of expert advisor programming. Examples for these aspects are strategies, indicators, money management and order filtering.

Expert advisors written with trading4j can place orders through the well known trading terminal <b>MetaTrader</b>. Therefore a wide range of brokers is supported while still having all the benefits of programming on the Java platform.

# Getting Started
To get started with programming with trading4j take a look at the <b>examples</b> directory. The example contains a server that serves an exemplary expert advisor.

The directory <b>client-metatrader</b> contains a MetaTrader compatible expert advisor. This expert advisor will connect to the server created with trading4j to delegate the trading decisions. To install it, build the Maven project <b>client-metatrader</b>. At the moment you need a C/Windows cross-compiler named <b>i686-w64-mingw32-gcc</b>. In Ubuntu 16.04 you can get this by installing the package <b>mingw-w64</b>. After the build you get a ZIP bundle in the target folder. Unzip it in the MetaTrader directory. Open the MetaEditor and compile the newly installed expert advisor <b>Trading4jRemoteExpertAdvisor.mq4</b>. After that, the expert advisor is available in MetaTrader terminal, e.g. for backtesting or live trading.

In future a pre-compiled version of the client is planned to simplify the setup of the client for MetaTrader.

# License
Trading4j can be used under the terms of the <b>GNU General Public License Version 3</b>.  