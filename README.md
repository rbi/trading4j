# Trading4j
Trading4j is a library that allows to write automated expert advisors for Forex trading. The library promotes writing code that is reusable across different expert advisors by clearly separating between different aspects of expert advisor programming. In Trading4j these aspects are strategies, indicators, money management and order filtering.

Expert advisors written with trading4j can place orders through the well known trading terminal *MetaTrader*. Therefore a wide range of brokers is supported while still having all the benefits of programming on the Java platform.

# License
Trading4j can be used under the terms of the [**GNU General Public License Version 3**](LICENSE.md).

# Getting Started
To get started with programming with trading4j take a look at the [examples](examples/) directory.
The example contains a server that serves an exemplary expert advisor.

To connect MetaTrader to the server a Trading4j client must be installed.
It can be downloaded from the releases tab.
Unzip the client into the MetaTrader directory.
The client should now be available as an expert advisor in MetaTrader terminal, e.g. for backtesting or live trading.
The Import of DLLs option must be enabled in MetaTrader for Trading4j to function properly.

On the Java side, Trading4j is available as Maven artifact with the following Maven coordinates.


    <dependency>
      <groupId>de.voidnode.trading4j</groupId>
      <artifactId>server</artifactId>
      <version>[some_version]</version>
    </dependency> 
    
The newest version available can be found on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.voidnode.trading4j%22%20AND%20a%3A%22server%22).

# Compiling the client for MetaTrader 4

The directory *client-metatrader* contains a MetaTrader compatible expert advisor. This expert advisor will connect to the server created with trading4j to delegate the trading decisions. To build it, enable the Maven project *client-metatrader* by activating the Maven Profile *client-metatrader*. At the moment you need a C/Windows cross-compiler named *i686-w64-mingw32-gcc*. In Ubuntu 16.04 you can get this by installing the package *mingw-w64*. You also need *wine* and a *MetaTrader* installation. You need to set up the Maven property *metaeditor.executable* (e.g. in your *settings.xml*) to point to the *metaeditor.exe* executable. A working Maven comand line could look as the following: `mvn -Pclient-metatrader -Dmetaeditor.executable="~/.wine/Program Files (x86)/MetaTrader 4/metaeditor.exe" install`.

After the build you get the client as a ZIP bundle in the target folder.