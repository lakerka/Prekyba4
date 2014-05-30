package handlers;

import interfaces.IHistoricalDataHandler;
import interfaces.IRealTimeBarHandler;

import java.util.HashMap;
import java.util.Map.Entry;

import model.Ticker;

import com.ib.controller.Bar;
import com.ib.controller.NewContract;
import com.ib.controller.Types.WhatToShow;

import controllers.ConnectionController;

public class RealTimeBarsDataResultPanelsHandler {

    private final HashMap<Integer, IRealTimeBarHandler> realTimeBarMap = new HashMap<Integer, IRealTimeBarHandler>();

    private ConnectionController connectionController;

    public RealTimeBarsDataResultPanelsHandler(ConnectionController connectionController) {

        if (connectionController == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        try {

            this.connectionController = connectionController;

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void reqRealTimeBars(Ticker ticker, NewContract contract,
            WhatToShow whatToShow, boolean rthOnly, IRealTimeBarHandler handler) {
        
        int reqId = ticker.getNewTickerId();
        
        realTimeBarMap.put(reqId, handler);
        
        connectionController.reqRealTimeBars(reqId, contract.getContract(), 0,
                whatToShow.toString(), rthOnly);
    }

    public void cancelRealtimeBars(IRealTimeBarHandler handler) {
        
        Integer reqId = getAndRemoveKey(realTimeBarMap, handler);
        
        if (reqId != null) {
            connectionController.cancelRealTimeBars(reqId);
        }
    }
    
    private static <K, V> K getAndRemoveKey(HashMap<K, V> map, V value) {

        for (Entry<K, V> entry : map.entrySet()) {

            if (entry.getValue() == value) {

                map.remove(entry.getKey());

                return entry.getKey();
            }
        }

        return null;
    }

}
