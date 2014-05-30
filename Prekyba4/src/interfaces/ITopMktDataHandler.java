package interfaces;

import com.ib.controller.NewTickType;
import com.ib.controller.Types.MktDataType;

public interface ITopMktDataHandler {
    
    void tickPrice(NewTickType tickType, double price, int canAutoExecute);
    
    void tickSize(NewTickType tickType, int size);
    
    void tickString(NewTickType tickType, String value);
    
    void tickSnapshotEnd();
    
    void marketDataType(MktDataType marketDataType);
}
