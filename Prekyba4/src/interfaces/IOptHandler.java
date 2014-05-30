package interfaces;

import com.ib.controller.NewTickType;

public interface IOptHandler extends ITopMktDataHandler {
    void tickOptionComputation( NewTickType tickType, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice);
}