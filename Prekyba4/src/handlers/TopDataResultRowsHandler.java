package handlers;

import initial.IllegalArgumentsValidator;
import initial.Messenger;
import interfaces.IHistoricalDataHandler;
import interfaces.IOptHandler;
import interfaces.ITopMktDataHandler;

import java.util.HashMap;
import java.util.Map.Entry;

import model.Ticker;

import com.ib.controller.NewContract;
import com.ib.controller.Types.MktDataType;

import controllers.ConnectionController;
import controllers.MainController;

public class TopDataResultRowsHandler {

    private final HashMap<Integer, ITopMktDataHandler> topMarketRowsMap = new HashMap<Integer, ITopMktDataHandler>();
    private final HashMap<Integer, IOptHandler> topOptionRowsMap = new HashMap<Integer, IOptHandler>();

    MainController mainController;

    public TopDataResultRowsHandler(MainController mainController) {

        IllegalArgumentsValidator.checkNullPointerPassed(mainController);

        try {
            this.mainController = mainController;

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    private static <K, V> K getAndRemoveKey(HashMap<K, V> map, V value) {

        for (Entry<K, V> entry : map.entrySet()) {

            if (entry.getValue() == value) {

                map.remove(entry.getKey());

                return entry.getKey();
            }
        }

        return null;
    }

    // get by tickerId
    public ITopMktDataHandler getITopMktDataHandler(int tickerId) {

        return this.topMarketRowsMap.get(tickerId);

    }

    public void reqTopMktData(NewContract contract, String genericTickList,
            boolean snapshot, ITopMktDataHandler handler) {

        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }
        
        int reqId = mainController.getRequestTicker().getNewTickerId();
        topMarketRowsMap.put(reqId, handler);
        mainController.getConnectionController().reqMktData(reqId,
                contract.getContract(), genericTickList, snapshot);
        // sendEOM();
    }

    public void reqOptionMktData(NewContract contract, String genericTickList,
            boolean snapshot, IOptHandler handler) {

        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }
        
        int reqId = mainController.getRequestTicker().getNewTickerId();

        topMarketRowsMap.put(reqId, handler);
        topOptionRowsMap.put(reqId, handler);

        mainController.getConnectionController().reqMktData(reqId,
                contract.getContract(), genericTickList, snapshot);
        // sendEOM();
    }

    public void cancelTopMktData(ITopMktDataHandler handler) {

        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }
        
        Integer reqId = getAndRemoveKey(topMarketRowsMap, handler);

        if (reqId != null) {

            mainController.getConnectionController().cancelMktData(reqId);

        } else {

            mainController.getMessenger().show(
                    "Top market data error: could not cancel top market data");
        }
        // sendEOM();
    }

    public void cancelOptionMktData(IOptHandler handler) {

        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }

        cancelTopMktData(handler);
        getAndRemoveKey(topOptionRowsMap, handler);
    }

    public void reqMktDataType(MktDataType type) {
        
        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }
        
        mainController.getConnectionController().reqMarketDataType(type.ordinal());
        // sendEOM();
    }

}
