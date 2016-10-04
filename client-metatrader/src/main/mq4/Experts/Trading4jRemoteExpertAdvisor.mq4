#property copyright "Copyright 2016, Raik Bieniek"
#property link      "https://github.com/rbi/trading4j"
#property version   "1.00"
#property strict

//--- input parameters
input int      indicatorNumber;

#include "../Trading4j/RemoteExpertAdvisor.mqh"

RemoteExpertAdvisor remoteAdvisor(indicatorNumber);

int OnInit() {
    if(remoteAdvisor.connect() != 0) {
        return(INIT_FAILED);
    }
    return(INIT_SUCCEEDED);
}
  
void OnDeinit(const int reason) {
    remoteAdvisor.disconnect();
}

void OnTick() {
    // neccessary for back tests
    remoteAdvisor.onTimer();
}

void OnTimer() {
    remoteAdvisor.onTimer();
}