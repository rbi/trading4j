# Trading4j
Trading4j is a library that allows to write automated expert advisors for Forex trading. The library promotes writing code that is reusable across different expert advisors by clearly separating between different aspects of expert advisor programming. Examples for these aspects are strategies, indicators, money management and order filtering.

Expert advisors written with trading4j can place orders through the well known trading terminal *MetaTrader*. Therefore a wide range of brokers is supported while still having all the benefits of programming on the Java platform.

# Getting Started
To get started with programming with trading4j take a look at the *examples* directory. The example contains a server that serves an exemplary expert advisor.

The directory *client-metatrader* contains a MetaTrader compatible expert advisor. This expert advisor will connect to the server created with trading4j to delegate the trading decisions. To install it, enable building the Maven project *client-metatrader* by activating the Maven Profile *client-metatrader*. At the moment you need a C/Windows cross-compiler named *i686-w64-mingw32-gcc*. In Ubuntu 16.04 you can get this by installing the package *mingw-w64*. You also need *wine* and a *MetaTrader* installation. You need to set the Maven property *metaeditor.executable* (e.g. in your *settings.xml*) to point to the *metaeditor.exe* executable. A working Maven comand line could look as the following: `mvn -Pclient-metatrader -Dmetaeditor.executable="~/.wine/Program Files (x86)/MetaTrader 4/metaeditor.exe" install`.

After the build you get a ZIP bundle in the target folder. Unzip it in the MetaTrader directory. The expert advisor should now be available in MetaTrader terminal, e.g. for backtesting or live trading.

In future a pre-compiled version of the client is planned to simplify the setup of the client for MetaTrader.

# License
Trading4j can be used under the terms of the **GNU General Public License Version 3**.  