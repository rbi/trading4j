#property copyright "Copyright 2016, Raik Bieniek"
#property link      "https://github.com/rbi/trading4j"
#property strict

#define int32 int
#define int64 long
#define byte unsigned char
#define Connection int
#define NO_MSG 255

#import "trading4j-client.dll"
int vnConnect(const uchar &host[], unsigned short port);
int vnDisconnect(const int connection);

byte vnReadByte(const int connection);
int32 vnReadInt32(const int connection);
int64 vnReadInt64(const int connection);
double vnReadDouble(const int connection);

int vnWriteBuffer(const int connection, const int buffer, const int bufferSize);

int vnAllocateBuffer(const int length);
void vnDeallocateBuffer(const int buffer);
void vnWriteConvertedInt32ToBuffer(const int buffer, const int offset, const int32 int32ToWrite);
void vnWriteConvertedInt64ToBuffer(const int buffer, const int offset, const int64 int64ToWrite);
void vnWriteConvertedDoubleToBuffer(const int buffer, const int offset, const double doubleToWrite);
void vnWriteByteToBuffer(const int buffer, const int offset, unsigned char byteToWrite);
void vnWriteStringToBuffer(const int buffer, const int offset, string stringToWrite);
#import


#define HOST "127.0.0.1"
#define PORT 6474

enum MessageNumbers {
    REQUEST_TRADING_ALGORITHM = 0,
    NEW_MARKET_DATA_SIMPLE    = 1,
    TREND_FOR_MARKET_DATA     = 2,
    PLACE_PENDING_ORDER       = 3,
    RESPONSE_PLACE_PENDING_ORDER        = 4,
    PENDING_ORDER_CONDITIONALY_EXECUTED = 5,
    PENDING_ORDER_CONDITIONALY_CLOSED   = 6,
    CLOSE_OR_CANCEL_PENDING_ORDER       = 7,
    EVENT_HANDLING_FINISHED             = 8,
    NEW_MARKET_DATA_EXTENDED            = 9,
    TRADING_ENVIRONMENT_INFORMATION     = 10,
    CHANGE_CLOSE_CONDITIONS             = 11,
    RESPONSE_CHANGE_CLOSE_CONDITIONS    = 12,
    BALANCE_CHANGED = 13,
    ACCOUNT_CURRENCY_EXCHANGE_RATE_CHANGED = 14,
};

enum VnOrderType {
    BUY = 0,
    SELL = 1,
};

enum VnExecutionCondition {
    DIRECT = 0,
    LIMIT = 1,
    STOP = 2,
};

enum Trend {
    UP      = 0,
    DOWN    = 1,
    UNKNOWN = 2,
};

struct DatedCandleStick {
    unsigned char messageNumber;
    datetime time;
    double open;
    double high;
    double low;
    double close;
};

struct FatCandleStick {
    unsigned char messageNumber;
    datetime time;
    double open;
    double high;
    double low;
    double close;
    int32 spread;
    int32 volume;
    int32 tickCount;
};

struct PendingOrder {
    int volume;
    VnOrderType type;
    VnExecutionCondition executionCondition;
    double entryPrice;
    double takeProfit;
    double stopLoose;
    bool hasExpirationDate;
    datetime expirationDate;
};

struct CloseConditionsToChange {
    int orderId;
    double stopLoose;
    double takeProfit;
    datetime expirationDate;
};

class MessageIo {
public:
    MessageIo();
    ~MessageIo();
    int connect();
    void disconnect();
    void writeRequestTradingAlgorithm(const int algorithmType, const int algorithmNumber);
    void writeTradingEnvironmentInformation(const string brokerName, const long accountNumber, const string accountCurrency,
            const string symbol, const string accountCurrencyExchangesymbol, const int markup, const int commission,
            const long nextNonHistoricCandleTime, const long minVolume, const long maxVolume, const long volumeStep);
    void writeNewMarketDataSimple(DatedCandleStick &sticks[]);
    void writeNewMarketDataExtended(FatCandleStick &sticks[]);
    void writeAccountCurrencyExchangeRate(const double accountCurrencyExchangeRate);
    void writeResponsePendingOrder(const bool success, const int idOrErrorCode);
    void writeOrderOpened(const int pendingOrderId, const long openTime, const double openingPrice);
    void writeOrderClosed(const int pendingOrderId, const long closeTime, const double closePrice);
    void writeResponsePendingOrderPendingOrder(const bool succeed, const int errorCode);
    void writeCurrentBalance(const long currentBalance);
    Trend readTrend();
    bool tryReadEventHandlingFinished();
    bool tryReadPendingOrder(PendingOrder & output);
    int tryReadCloseOrCancelPendingOrder();
    bool tryReadChangeCloseConditions(CloseConditionsToChange & output);
private: 
    Connection connection;
    int msgNr;
    void readMsgNumberIfNeccessary();
};

MessageIo::MessageIo() {
    msgNr = NO_MSG;
}

MessageIo::~MessageIo() {

}

int MessageIo::connect() {
    uchar host[StringLen(HOST)];
    StringToCharArray(HOST, host);
    connection = vnConnect(host, PORT);
    if(connection < 0) {
        printf("Connecting to the trading strategy server failed with error code %d.", connection);
        return -1;
    }
    return 0;
}

void MessageIo::disconnect() {
    vnDisconnect(connection);
}

Trend MessageIo::readTrend() {
    vnReadByte(connection); //ignore message number of a new trend
    return (Trend)vnReadByte(connection);
}

void MessageIo::writeTradingEnvironmentInformation(const string brokerName, const long accountNumber, const string accountCurrency,
        const string symbol, const string accountCurrencyExchangesymbol, const int markup, const int commission, const long nextNonHistoricCandleTime,
        const long minVolume, const long maxVolume, const long volumeStep) {
    //FIXME the following assumes that each character has the size of 1 byte which is not true for many UTF-8 characters
    int brokerNameSize = StringLen(brokerName) + 2;
    int currencySize = StringLen(accountCurrency) + 2;
    int symbolSize = StringLen(symbol) + 2;
    int accountSymbolSize = StringLen(accountCurrencyExchangesymbol) + 2;
    
    int bufferSize = brokerNameSize + currencySize + symbolSize + accountSymbolSize + 49;
    int buffer = vnAllocateBuffer(bufferSize);
    
    // message number + account information
    vnWriteByteToBuffer(buffer, 0, TRADING_ENVIRONMENT_INFORMATION);
    vnWriteStringToBuffer(buffer, 1, brokerName);
    vnWriteConvertedInt64ToBuffer(buffer, brokerNameSize + 1, accountNumber);
    vnWriteStringToBuffer(buffer, brokerNameSize + 9, accountCurrency);
    
    // misc information
    int offset = brokerNameSize + 9 + currencySize;
    vnWriteStringToBuffer(buffer, offset, symbol);
    vnWriteStringToBuffer(buffer, offset + symbolSize, accountCurrencyExchangesymbol);
    vnWriteConvertedInt32ToBuffer(buffer, offset + symbolSize + accountSymbolSize, markup);
    vnWriteConvertedInt32ToBuffer(buffer, offset + symbolSize + accountSymbolSize + 4, commission);
    vnWriteConvertedInt64ToBuffer(buffer, offset + symbolSize + accountSymbolSize + 8, nextNonHistoricCandleTime);
    
    // volume constraint information
    offset += symbolSize + accountSymbolSize +16;
    vnWriteConvertedInt64ToBuffer(buffer, offset, minVolume);
    vnWriteConvertedInt64ToBuffer(buffer, offset + 8, maxVolume);
    vnWriteConvertedInt64ToBuffer(buffer, offset + 16, volumeStep);
    
    vnWriteBuffer(connection, buffer, bufferSize);
    vnDeallocateBuffer(buffer);
}

void MessageIo::writeNewMarketDataSimple(DatedCandleStick &sticks[]) {

    int bufferSize = ArraySize(sticks) * sizeof(DatedCandleStick);
    int buffer = vnAllocateBuffer(bufferSize);

    for(int i = 0; i < ArraySize(sticks); i++) {
        int offset = i * sizeof(DatedCandleStick);
        vnWriteByteToBuffer(buffer, offset, NEW_MARKET_DATA_SIMPLE);
        vnWriteConvertedInt64ToBuffer (buffer, offset + 1 + 0 * 8, sticks[i].time);
        vnWriteConvertedDoubleToBuffer(buffer, offset + 1 + 1 * 8, sticks[i].open);
        vnWriteConvertedDoubleToBuffer(buffer, offset + 1 + 2 * 8, sticks[i].high);
        vnWriteConvertedDoubleToBuffer(buffer, offset + 1 + 3 * 8, sticks[i].low);
        vnWriteConvertedDoubleToBuffer(buffer, offset + 1 + 4 * 8, sticks[i].close);
    }
    
    vnWriteBuffer(connection, buffer, bufferSize);
    vnDeallocateBuffer(buffer);
}

void MessageIo::writeNewMarketDataExtended(FatCandleStick &sticks[]) {

    int bufferSize = ArraySize(sticks) * sizeof(FatCandleStick);
    int buffer = vnAllocateBuffer(bufferSize);

    for(int i = 0; i < ArraySize(sticks); i++) {
        int offset = i * sizeof(FatCandleStick);
        //printf("LAST OFFSET "+1 + 6 * 8 + 2 * 4);
        vnWriteByteToBuffer(buffer, offset, NEW_MARKET_DATA_EXTENDED);
        vnWriteConvertedInt64ToBuffer (buffer, offset + 1 + 0 * 8, sticks[i].time);
        vnWriteConvertedDoubleToBuffer(buffer, offset + 1 + 1 * 8, sticks[i].open);
        vnWriteConvertedDoubleToBuffer(buffer, offset + 1 + 2 * 8, sticks[i].high);
        vnWriteConvertedDoubleToBuffer(buffer, offset + 1 + 3 * 8, sticks[i].low);
        vnWriteConvertedDoubleToBuffer(buffer, offset + 1 + 4 * 8, sticks[i].close);
        vnWriteConvertedInt32ToBuffer(buffer, offset + 1 + 5 * 8, sticks[i].spread);
        vnWriteConvertedInt32ToBuffer(buffer, offset + 1 + 5 * 8 + 1 * 4, sticks[i].volume);
        vnWriteConvertedInt32ToBuffer(buffer, offset + 1 + 5 * 8 + 2 * 4, sticks[i].tickCount);
    }
    
    vnWriteBuffer(connection, buffer, bufferSize);
    vnDeallocateBuffer(buffer);
}

void MessageIo::writeAccountCurrencyExchangeRate(const double accountCurrencyExchangeRate) {
    int buffer = vnAllocateBuffer(9);
    
    vnWriteByteToBuffer(buffer, 0, ACCOUNT_CURRENCY_EXCHANGE_RATE_CHANGED);
    vnWriteConvertedDoubleToBuffer(buffer, 1, accountCurrencyExchangeRate);
    
    vnWriteBuffer(connection, buffer, 9);
    vnDeallocateBuffer(buffer);
}

void MessageIo::writeRequestTradingAlgorithm(const int algorithmType, const int algorithmNumber) {
    int buffer = vnAllocateBuffer(2);
    
    vnWriteByteToBuffer(buffer, 0, REQUEST_TRADING_ALGORITHM);
    vnWriteByteToBuffer(buffer, 1, (unsigned char)algorithmType);
    vnWriteConvertedInt32ToBuffer(buffer, 2, algorithmNumber);
    
    vnWriteBuffer(connection, buffer, 6);
    vnDeallocateBuffer(buffer);
}

void MessageIo::writeResponsePendingOrder(const bool success, const int idOrErrorCode) {
    int buffer = vnAllocateBuffer(6);
    vnWriteByteToBuffer(buffer, 0, RESPONSE_PLACE_PENDING_ORDER);
    vnWriteByteToBuffer(buffer, 1, (byte)(!success));

    vnWriteConvertedInt32ToBuffer(buffer, 2, idOrErrorCode);
    
    vnWriteBuffer(connection, buffer, 6);
    vnDeallocateBuffer(buffer);
}

void MessageIo::writeOrderOpened(const int pendingOrderId, const long openTime, const double openingPrice) {
    int buffer = vnAllocateBuffer(21);
    vnWriteByteToBuffer(buffer, 0, PENDING_ORDER_CONDITIONALY_EXECUTED);
    vnWriteConvertedInt32ToBuffer(buffer, 1, pendingOrderId);
    vnWriteConvertedInt64ToBuffer(buffer, 5, openTime);
    vnWriteConvertedDoubleToBuffer(buffer, 13, openingPrice);
    
    vnWriteBuffer(connection, buffer, 21);
    vnDeallocateBuffer(buffer);
}

void MessageIo::writeOrderClosed(const int pendingOrderId, const long closeTime, const double closingPrice) {
    int buffer = vnAllocateBuffer(21);
    vnWriteByteToBuffer(buffer, 0, PENDING_ORDER_CONDITIONALY_CLOSED);
    vnWriteConvertedInt32ToBuffer(buffer, 1, pendingOrderId);
    vnWriteConvertedInt64ToBuffer(buffer, 5, closeTime);
    vnWriteConvertedDoubleToBuffer(buffer, 13, closingPrice);
    
    vnWriteBuffer(connection, buffer, 21);
    vnDeallocateBuffer(buffer);
}

void MessageIo::writeResponsePendingOrderPendingOrder(const bool succeed, const int errorCode) {
    int buffer = vnAllocateBuffer(succeed? 2 : 6);
    
    vnWriteByteToBuffer(buffer, 0, RESPONSE_CHANGE_CLOSE_CONDITIONS);
    vnWriteByteToBuffer(buffer, 1, (byte)(!succeed));
    if(!succeed) {
        vnWriteConvertedInt32ToBuffer(buffer, 2, errorCode);
    }
    
    vnWriteBuffer(connection, buffer, (succeed? 2 : 6));
    vnDeallocateBuffer(buffer);
}

void MessageIo::writeCurrentBalance(const long currentBalance) {
    int buffer = vnAllocateBuffer(9);
    vnWriteByteToBuffer(buffer, 0, BALANCE_CHANGED);
    vnWriteConvertedInt64ToBuffer(buffer, 1, currentBalance);
    
    vnWriteBuffer(connection, buffer, 9);
    vnDeallocateBuffer(buffer);
}

bool MessageIo::tryReadEventHandlingFinished() {
    readMsgNumberIfNeccessary();
    
    if(msgNr == EVENT_HANDLING_FINISHED) {
        msgNr = NO_MSG;
        return true;
    } else {
        return false;
    }
}

bool MessageIo::tryReadPendingOrder(PendingOrder & output) {
    readMsgNumberIfNeccessary();
    
    if(msgNr != PLACE_PENDING_ORDER) {
        return false;
    }
    
    msgNr = NO_MSG;
    byte flags = vnReadByte(connection);
    output.type = (VnOrderType)(flags & 1);
    output.executionCondition = (VnExecutionCondition)((flags >> 1) & 3);
    output.hasExpirationDate = ((flags >> 3) & 1) != 0;
    
    output.volume = vnReadInt32(connection);
    output.entryPrice = vnReadDouble(connection);
    output.takeProfit = vnReadDouble(connection);
    output.stopLoose = vnReadDouble(connection);
    if(output.hasExpirationDate) {
        output.expirationDate = vnReadInt64(connection) - TimeGMTOffset();
    }

    return true;
}

int MessageIo::tryReadCloseOrCancelPendingOrder() {
    readMsgNumberIfNeccessary();
    
    if(msgNr != CLOSE_OR_CANCEL_PENDING_ORDER) {
        return -1;
    }

    msgNr = NO_MSG;
    return vnReadInt32(connection);
}

bool MessageIo::tryReadChangeCloseConditions(CloseConditionsToChange & output) {
    readMsgNumberIfNeccessary();
    
    if(msgNr != CHANGE_CLOSE_CONDITIONS) {
        return false;
    }
    
    msgNr = NO_MSG;
    
    bool hasDate = vnReadByte(connection);
    output.orderId = vnReadInt32(connection);
    output.takeProfit = vnReadDouble(connection);
    output.stopLoose = vnReadDouble(connection);
    
    if(hasDate) {
        output.expirationDate = vnReadInt64(connection) - TimeGMTOffset();  
    } else {
        output.expirationDate = 0;
    }

    return true;
}

void MessageIo::readMsgNumberIfNeccessary() {
    if(msgNr == NO_MSG) {
        msgNr = (int) vnReadByte(connection);
    }
}