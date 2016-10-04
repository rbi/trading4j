#property copyright "Copyright 2016, Raik Bieniek"
#property link      "https://github.com/rbi/trading4j"
#property version   "1.00"
#property strict

#include "MessageHandling.mqh";

/**
 * Communicates with a remote indicator over a TCP/IP connection.
 */
class RemoteIndicator {
public:
    RemoteIndicator(const int indicator, const bool absoluteValueForTrend);
    ~RemoteIndicator();
    int connect();
    int onCalculate (double &trendBuffer[], const int rates_total, const datetime &time[],
        const double &open[], const double &high[], const double &low[], const double &close[]);
private:
    MessageIo messageIo;
    const int indicator;
    const bool absoluteValueForTrend;
    int lastRatesTotal;
};

RemoteIndicator::RemoteIndicator(const int _indicator, const bool _absoluteValueForTrend) : indicator(_indicator)
        , absoluteValueForTrend(_absoluteValueForTrend), messageIo() {
    lastRatesTotal = 1;
}

RemoteIndicator::~RemoteIndicator() {
    messageIo.disconnect();
}

int RemoteIndicator::connect() {
    int returnVal = messageIo.connect();
    messageIo.writeRequestTradingAlgorithm(1, indicator);
    return returnVal;
}

int RemoteIndicator::onCalculate (double &trendBuffer[], const int rates_total, const datetime &time[],
        const double &open[], const double &high[], const double &low[], const double &close[]) {
        
    if(rates_total == lastRatesTotal) {
        return rates_total;
    }
    
    int candleCount = (rates_total - lastRatesTotal);
    DatedCandleStick sticks[];
    ArrayResize(sticks, candleCount);
    
    int firstIndex = rates_total - lastRatesTotal;
    
    for(int i = firstIndex; i >= 1; i--) {  
        //printf("writing open %f, high %f, low %f, close %f of time "+time[i], open[i], high[i], low[i], close[i]);
        int current = (firstIndex - i);
        sticks[current].messageNumber = NEW_MARKET_DATA_SIMPLE;
        sticks[current].time = time[i] + TimeGMTOffset();
        sticks[current].open = open[i];
        sticks[current].high = high[i];
        sticks[current].low = low[i];
        sticks[current].close = close[i];
    }
    messageIo.writeNewMarketDataSimple(sticks);
    ArrayFree(sticks);
    
    for(int i = firstIndex; i >= 1; i--) {
        Trend trend = messageIo.readTrend();
        if (trend == UP) {
            if(absoluteValueForTrend) {
                trendBuffer[i] = 1.0;
            } else {
                trendBuffer[i] = high[i];
            }
        } else if (trend == DOWN) {
            if(absoluteValueForTrend) {
                trendBuffer[i] = -1.0;
            } else {
                trendBuffer[i] = low[i];
            }
        } else if(absoluteValueForTrend) {
            trendBuffer[i] = 0.0;
        }
    }
    
    lastRatesTotal = rates_total;
    return rates_total;
}