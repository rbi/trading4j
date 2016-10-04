#property copyright "Copyright 2016, Raik Bieniek"
#property link      "https://github.com/rbi/trading4j"
#property strict

int  calculateCommission() {
   if(!brokerChargesCommission()) {
      return 0;
   }
   
   if(isCheapSymbol()) {
      return 3 * MathPow(10, 5-Digits);
   }
   
      return 4 * MathPow(10, 5-Digits);
}

bool brokerChargesCommission() {
    if(IsTesting()) {
      return false;
    }
    
    if(StringFind(AccountCompany(), "Forex Capital Markets") != -1)  {
      return true;
    }
    return false;
}

bool isCheapSymbol() {
   if(symbolIs("EURUSD") ||
     symbolIs("GBPUSD") ||
     symbolIs("USDJPY") ||
     symbolIs("USDCHF") ||
     symbolIs("AUDUSD") ||
     symbolIs("EURJPY") ||
     symbolIs("GBPJPY")) {
       return true;
     }
     return false;
}

bool symbolIs(string should) {
  return StringCompare(Symbol(), should) == 0;
}