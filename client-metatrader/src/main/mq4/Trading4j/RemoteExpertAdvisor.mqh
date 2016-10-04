#property copyright "Copyright 2016, Raik Bieniek"
#property link      "https://github.com/rbi/trading4j"
#property strict

#include <Arrays/ArrayInt.mqh>;
#include "MessageHandling.mqh";
#include "Commission.mqh";

#define SLIPPAGE 3

/**
 * Communicates with a remote expert advisor over a TCP/IP connection.
 */
class RemoteExpertAdvisor {
public:
    RemoteExpertAdvisor(const int expertAdvisorNumber);
    ~RemoteExpertAdvisor();
    int connect();
    void onTimer();
    void disconnect();
private:
    MessageIo messageIo;
    const int expertAdvisorNumber;
    MqlRates lastTradedSymbolRate;
    MqlRates lastAccountCurrencyExchangeSymbolRate;
    CArrayInt ordersPending;
    CArrayInt ordersOpened;
    bool eaFailed;
    const string accountCurrencyExchangeSymbol;
    int lastRemotelyClosedOrderId;
    void sendRatesIfNeeded();
    void sendRate(const MqlRates & rate);
    void sendOpenedOrdersIfNeeded();
    void sendClosedOrdersIfNeeded();
    void sendAccountCurrencyExchangeRateIfNeeded();
    void sendCurrentBalance();
    void readResponse();
    int calculateMarkup();
    long calculateNextCandleTime();
    string calculateAccountCurrencyExchangeSymbol();
    void sendHistoricData();
    void handleNewPendingOrder(PendingOrder & order);
    void handleClosePendingOrder(const int orderId);
    bool equal(const string string1, const string string2);
};

RemoteExpertAdvisor::RemoteExpertAdvisor(const int _expertAdvisorNumber)
    : expertAdvisorNumber(_expertAdvisorNumber), eaFailed(false), lastRemotelyClosedOrderId(-1), accountCurrencyExchangeSymbol(calculateAccountCurrencyExchangeSymbol()) {
    lastTradedSymbolRate.time = 0;
    lastAccountCurrencyExchangeSymbolRate.time = 0;
}

RemoteExpertAdvisor::~RemoteExpertAdvisor() {

}

int RemoteExpertAdvisor::connect() {
    if(eaFailed) {
        return -1;
    }
    
    int connectStatus =  messageIo.connect();
    messageIo.writeRequestTradingAlgorithm(0, expertAdvisorNumber);
    
    const string accountCompany = IsTesting() ? "Backtest" : AccountCompany();
    const long accountNumber = IsTesting() ? 0 : AccountNumber();

    messageIo.writeTradingEnvironmentInformation(accountCompany, accountNumber, AccountCurrency(),
        Symbol(), accountCurrencyExchangeSymbol, calculateMarkup(), calculateCommission(), calculateNextCandleTime(),  MarketInfo(Symbol(),
        MODE_MINLOT) * 100000, MarketInfo(Symbol(), MODE_MAXLOT) * 100000,  MarketInfo(Symbol(), MODE_LOTSTEP) * 100000);
    
    sendCurrentBalance();
    sendAccountCurrencyExchangeRateIfNeeded();
    sendHistoricData();
    
    EventSetTimer(1);
    return connectStatus;
}

int RemoteExpertAdvisor::calculateMarkup() {
    long bid = Bid * 10000;
    long close = Close[0] * 10000;
    
    return close - bid;
}

long RemoteExpertAdvisor::calculateNextCandleTime() {
    MqlRates rates[1];
    CopyRates(Symbol(), PERIOD_M1, 1, 1, rates);
    
    // Add some buffer for the time it takes to transmit historic data.
    return rates[0].time  + TimeGMTOffset() + 120;
}

void RemoteExpertAdvisor::sendHistoricData() {
    MqlRates rates[];

    CopyRates(Symbol(), PERIOD_M1, 1, 
        30 * 24 * 60 * 60, // one month in the past
        rates);
        
    for (int i = 0; i < ArraySize(rates); i++) {
        sendRate(rates[i]);
    }
}

void RemoteExpertAdvisor::disconnect() {
    EventKillTimer();
    messageIo.disconnect();
}

void RemoteExpertAdvisor::onTimer() {
    if(eaFailed) {
        return;
    }
    sendOpenedOrdersIfNeeded();
    sendClosedOrdersIfNeeded();
    sendRatesIfNeeded();
    sendAccountCurrencyExchangeRateIfNeeded();
}

void RemoteExpertAdvisor::sendRatesIfNeeded() {
    MqlRates rates[1];
    CopyRates(Symbol(), PERIOD_M1, 1, 1, rates);

    if(rates[0].time <= lastTradedSymbolRate.time) {
        return;
    }
    
    sendRate(rates[0]);
    
    lastTradedSymbolRate = rates[0];
}

void RemoteExpertAdvisor::sendAccountCurrencyExchangeRateIfNeeded() {
    MqlRates rates[1];
    CopyRates(accountCurrencyExchangeSymbol, PERIOD_M1, 1, 1, rates);

    if(rates[0].time <= lastAccountCurrencyExchangeSymbolRate.time) {
        return;
    }
    
    messageIo.writeAccountCurrencyExchangeRate(rates[0].close);
    readResponse();
    
    lastAccountCurrencyExchangeSymbolRate = rates[0];
}

void RemoteExpertAdvisor::sendRate(const MqlRates &rate) {
    FatCandleStick stick[1];
    stick[0].time = rate.time + TimeGMTOffset();
    stick[0].open = rate.open;
    stick[0].high = rate.high;
    stick[0].low = rate.low;
    stick[0].close = rate.close;
    stick[0].spread = rate.spread;
    stick[0].volume = rate.real_volume;
    stick[0].tickCount = rate.tick_volume;

    if (stick[0].spread == 0
        || stick[0].spread > 1000000) { // spread for historic data is unreasonable high
        //When the spread is not set it via the rates array
        stick[0].spread = MarketInfo(Symbol(), MODE_SPREAD);
    }
    
    // convert the spread in points to a Price with fixed decimal count of 5
    stick[0].spread = stick[0].spread * MathPow(10, 5-Digits);
    
    messageIo.writeNewMarketDataExtended(stick);
    readResponse();

}

void RemoteExpertAdvisor::sendOpenedOrdersIfNeeded() {
    for(int i = 0; i < ordersPending.Total(); i++) {
        int current = ordersPending.At(i);
        OrderSelect(current, SELECT_BY_TICKET, MODE_TRADES);
        if (OrderType() == OP_BUY || OrderType() == OP_SELL) {
            messageIo.writeOrderOpened(current, OrderOpenTime() + TimeGMTOffset(), OrderOpenPrice());
            sendCurrentBalance();
            readResponse();
            
            ordersPending.Delete(i);
            ordersOpened.Add(current);
        }
    }
}

void RemoteExpertAdvisor::sendClosedOrdersIfNeeded() {
    for(int i = 0; i < ordersOpened.Total(); i++) {
        int current = ordersOpened.At(i);
        
        OrderSelect(current, SELECT_BY_TICKET, MODE_HISTORY);
        datetime closeTime = OrderCloseTime();
        if(closeTime != 0) {
            ordersOpened.Delete(i);
            if(current == lastRemotelyClosedOrderId) {
                // do not send close notifications on orders that where closed remotely
                lastRemotelyClosedOrderId = -1;
            } else {
                messageIo.writeOrderClosed(current, closeTime + TimeGMTOffset(), OrderClosePrice());
                sendCurrentBalance();
                readResponse();
            }
        }
    }
}

void RemoteExpertAdvisor::readResponse() {
    while(true) {
        PendingOrder order;
        if(messageIo.tryReadPendingOrder(order)) {
            handleNewPendingOrder(order);
            continue;
        }
        
        int orderNumber = messageIo.tryReadCloseOrCancelPendingOrder();
        if(orderNumber != -1) {
            handleClosePendingOrder(orderNumber);
            continue;
        }
        
        CloseConditionsToChange closeConditions;
        if(messageIo.tryReadChangeCloseConditions(closeConditions)) {
            OrderSelect(closeConditions.orderId, SELECT_BY_TICKET);
            bool changeSucced = OrderModify(closeConditions.orderId, OrderOpenPrice(), closeConditions.stopLoose, closeConditions.takeProfit,
                closeConditions.expirationDate);
            messageIo.writeResponsePendingOrderPendingOrder(changeSucced, GetLastError());
            if(!changeSucced) {
                printf("chaning close conditions pending order with id %d failed: take profit %f stop loose %f expariton date: "
                    + closeConditions.expirationDate, closeConditions.orderId, closeConditions.takeProfit, closeConditions.stopLoose);
            }
            continue;
        }
    
        if(messageIo.tryReadEventHandlingFinished()) {
            break;
        } else {
            printf("ERROR: The received data did not match any expected message. No more incomming messages will be processed.");
            disconnect();
            eaFailed = true;
            return;
        }
    }
}

void RemoteExpertAdvisor::handleNewPendingOrder(PendingOrder & order) {
    //printf("received pending order volume %d, order type %d, execution condition %d, entry price %f take profit %f stop loose %f has expiration date %d expariton date: "
    //        + order.expirationDate, order.volume, order.type, order.executionCondition, order.entryPrice, order.takeProfit, order.stopLoose, order.hasExpirationDate);
    datetime expirationDate = order.hasExpirationDate? order.expirationDate : 0;
    int tradeOperation;
    if(order.type == BUY) {
        if(order.executionCondition == LIMIT){
            tradeOperation = OP_BUYLIMIT;
        } else if (order.executionCondition == STOP){
            tradeOperation = OP_BUYSTOP;
        } else {
            tradeOperation = OP_BUY;
        }
    }
    else {
        if(order.executionCondition == LIMIT){
            tradeOperation = OP_SELLLIMIT;
        } else if (order.executionCondition == STOP){
            tradeOperation = OP_SELLSTOP;
        } else {
            tradeOperation = OP_SELL;
        }
    }
    
    int ticket = OrderSend(Symbol(), tradeOperation, (double)order.volume / 100000, NormalizeDouble(order.entryPrice, Digits()), SLIPPAGE,  NormalizeDouble(order.stopLoose, Digits()),
             NormalizeDouble(order.takeProfit, Digits()), NULL, 0, expirationDate);
    
    if(ticket != -1) {
        ordersPending.Add(ticket);
        messageIo.writeResponsePendingOrder(true, ticket);
    } else {
        messageIo.writeResponsePendingOrder(false, GetLastError());
        
        string condition =  order.executionCondition == LIMIT? "limit" : order.executionCondition == STOP? "stop": "direct";
        string type = order.type == BUY? "buy" : "sell";
        MqlRates rates[1];
        CopyRates(Symbol(), PERIOD_M1, 0, 1, rates);
        
        
        printf("pending order failed: current market price %.5f, volume %d, order type: %s %s, entry price %f take profit %f stop loose %f has expiration date %d expariton date: "
            + order.expirationDate, rates[0].close, order.volume, type, condition, order.entryPrice, order.takeProfit, order.stopLoose, order.hasExpirationDate);
    }
}

void RemoteExpertAdvisor::handleClosePendingOrder(const int orderId) {
    OrderSelect(orderId, SELECT_BY_TICKET);
    if(OrderType() == OP_BUY || OrderType() == OP_SELL) {
        //order was opened
        bool success;
        if(OrderType() == OP_BUY) {
            success = OrderClose(orderId, OrderLots(), Bid, SLIPPAGE);
        } else {
            success = OrderClose(orderId, OrderLots(), Ask, SLIPPAGE);
        }
        if(!success) {
            printf("ERROR: The active order with the id "+orderId+" could not be closed. It is no longer managed by the expert advisor!");
        }
        
        lastRemotelyClosedOrderId = orderId;
        
        for(int i = 0; i < ordersOpened.Total(); i++) {
            int current = ordersOpened.At(i);
            if(current == orderId) {
                ordersOpened.Delete(i);
                break;
            }
        }
        sendCurrentBalance();
    } else {
        //order is pending
        OrderDelete(orderId);
        
        for(int i = 0; i < ordersPending.Total(); i++) {
            int current = ordersPending.At(i);
            if(current == orderId) {
                ordersPending.Delete(i);
                break;
            }
        }
    }
}

void RemoteExpertAdvisor::sendCurrentBalance() {
   messageIo.writeCurrentBalance(AccountBalance() * 100);
   readResponse();
}

string RemoteExpertAdvisor::calculateAccountCurrencyExchangeSymbol() {
    const string accountCurrency = AccountCurrency();
    const string tradedSymbol = Symbol();
    const string tradedBaseCurrency = StringSubstr(tradedSymbol, 0, 3);
    const string tradedQuoteCurrency = StringSubstr(tradedSymbol, 3, 3);
    
    if(equal(accountCurrency, tradedBaseCurrency) || equal(accountCurrency, tradedQuoteCurrency)) {
        return tradedSymbol;
    }
    
    for(int i = 0; i < SymbolsTotal(false); i++) {
        const string name = SymbolName(i, false);
        if(StringLen(name) != 6) {
            continue; // not a forex symbol
        }
        const string baseCurrency = StringSubstr(name, 0, 3);
        const string quoteCurrency = StringSubstr(name, 3, 3);
        
        if(equal(accountCurrency, baseCurrency) && equal(tradedQuoteCurrency, quoteCurrency)) {
            return name;
        }
        
        if(equal(accountCurrency, quoteCurrency) && equal(tradedQuoteCurrency, baseCurrency)) {
            return name;
        }
    }
    
    printf("ERROR: The exchange rate symbol converting the account currency " + accountCurrency
            + " to the quote currency of the traded symbol " + tradedSymbol + " was not found.");
    disconnect();
    eaFailed = true;
    return "";      
}

bool RemoteExpertAdvisor::equal(const string string1, const string string2) {
    return StringCompare(string1, string2) == 0;
}