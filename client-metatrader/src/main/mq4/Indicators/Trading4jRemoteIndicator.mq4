#property copyright "Copyright 2016, Raik Bieniek"
#property link      "https://github.com/rbi/trading4j"
#property version   "1.00"
#property strict

#property indicator_chart_window
//#property indicator_separate_window
#property indicator_buffers 1
#property indicator_plots   1
#property indicator_label1  "Remote Indicator"
#property indicator_type1   DRAW_ARROW
#property indicator_color1  clrRed
//#property indicator_style1  STYLE_SOLID
#property indicator_width1  5

//--- input parameters
input int      indicatorNumber;

#include "../Trading4j/RemoteIndicator.mqh"

double remoteIndicatorTrendBuffer[];
RemoteIndicator remoteIndicator(indicatorNumber, false);

int OnInit() {
    int succeed = remoteIndicator.connect();
    if(succeed != 0) {
        return(INIT_FAILED);
    }
    SetIndexBuffer(0, remoteIndicatorTrendBuffer);
    return(INIT_SUCCEEDED);
}

int OnCalculate(const int rates_total,
                const int prev_calculated,
                const datetime &time[],
                const double &open[],
                const double &high[],
                const double &low[],
                const double &close[],
                const long &tick_volume[],
                const long &volume[],
                const int &spread[]) {
    return remoteIndicator.onCalculate(remoteIndicatorTrendBuffer, rates_total, time,
        open, high, low, close);
}