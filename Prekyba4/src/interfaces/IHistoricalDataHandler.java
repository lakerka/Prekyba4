package interfaces;

import com.ib.controller.Bar;

public interface IHistoricalDataHandler {
    
    void historicalData(Bar bar, boolean hasGaps);
    
    //cancel historical data request
    void historicalDataEnd();
}
