package handlers;

import java.util.HashMap;
import java.util.Map.Entry;

import model.Ticker;

import com.ib.controller.NewContract;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Types.WhatToShow;

import interfaces.IHistoricalDataHandler;
import controllers.ConnectionController;
import controllers.MainController;

/*
 Have all panels that are visualising historical
 data received from server. Panels are uniquelly identified
 by tickerId
 */
public class HistoricalDataResultPanelsHandler {

    private final HashMap<Integer, IHistoricalDataHandler> historicalDataMap = new HashMap<Integer, IHistoricalDataHandler>();

    private MainController mainController;

    public HistoricalDataResultPanelsHandler(MainController mainController) {

        if (mainController == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        try {
            this.mainController = mainController;

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    /**
     * @param endDateTime
     *            format is YYYYMMDD HH:MM:SS [TMZ]
     * @param duration
     *            is number of durationUnits
     * @param WhatToShow
     *            is what type of data you want to get: BID, ASK, ...
     * @param rthOnly
     *            RTH - regular trading hours <br>
     *            • 0 - all data is returned even where the market in question
     *            was outside of its regular trading hours. <br>
     *            • 1 - only data within the regular trading hours is returned,
     *            even if the requested time span falls partially or completely
     *            outside of the RTH.
     */

    public void reqHistoricalData(NewContract contract, String endDateTime,
            int duration, DurationUnit durationUnit, BarSize barSize,
            WhatToShow whatToShow, boolean rthOnly,
            IHistoricalDataHandler handler) {

        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }

        try {

            /*
             * request new data ticker id, because every data is identified by
             * this id
             */
            int tickerId = mainController.getRequestTicker().getTickerId();

            historicalDataMap.put(tickerId, handler);

            String durationStr = duration + " "
                    + durationUnit.toString().charAt(0);

            mainController.getConnectionController().reqHistoricalData(
                    tickerId, contract.getContract(), endDateTime, durationStr,
                    barSize.toString(), whatToShow.toString(), rthOnly ? 1 : 0,
                    2);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void cancelHistoricalData(IHistoricalDataHandler handler) {

        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }
        
        Integer reqId = getAndRemoveKey(historicalDataMap, handler);

        /*
         * send server cancelation message of requested data
         */
        if (reqId != null) {
            mainController.getConnectionController()
                    .cancelHistoricalData(reqId);
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

    // get IHistoricalDataHandler by tickerId
    public IHistoricalDataHandler getIHistoricalDataHandler(int tickerId) {

        return this.historicalDataMap.get(tickerId);

    }

}
