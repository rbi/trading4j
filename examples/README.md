# Trading4j Example
This project contains an exemplary expert advisor that is intended to demonstrate how to use the Trading4j library.
It consists of the following parts ordered top-down.

* ExpertAdvisorServer
    * This is the entry point to the program.
      It's purpose is to serve the trading strategy to a  MetaTrader client.
* NMovingAveragesExpertAdvisorFactory
    * This factory builds an exemplary expert advisor.
      It does not only set up the strategy itself but also all side aspects like order filters.
* NMovingAveragesExpertAdvisor
    * This is the trading strategy itself.
      It implements the TradingStrategy interface to be usable with the default state machine bundled with Trading4j.
      Using this state machine is fully optional.
* LowVolatilityOrderFilter
    * This is an example of an order filter.
      Order filters are used to implement detection of market conditions when trading should be prevented.
      This way the code of the  trading strategy stays focused on the strategy itself.