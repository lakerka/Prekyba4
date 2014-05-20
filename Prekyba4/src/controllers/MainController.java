package controllers;

import handlers.HistoricalDataResultPanelsHandler;
import handlers.LiveOrdersHandler;
import handlers.OrdersHandler;
import handlers.RealTimeBarsDataResultPanelsHandler;
import handlers.TopDataResultRowsHandler;
import handlers.TradeReportsHandler;
import initial.IllegalArgumentsValidator;
import initial.Messenger;
import interfaces.IHistoricalDataHandler;
import interfaces.ILiveOrderHandler;
import interfaces.IOrderHandler;
import interfaces.ITopMktDataHandler;
import interfaces.ITradeReportHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import model.Ticker;

import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.UnderComp;
import com.ib.controller.Bar;
import com.ib.controller.NewContract;
import com.ib.controller.NewOrder;
import com.ib.controller.NewOrderState;
import com.ib.controller.NewTickType;
import com.ib.controller.OrderStatus;
import com.ib.controller.Types.MktDataType;

public class MainController implements EWrapper {

    private HistoricalDataResultPanelsHandler historicalDataHandler;
    private RealTimeBarsDataResultPanelsHandler realTimeBarsHandler;
    private TopDataResultRowsHandler topDataResultRowsHandler;
    private LiveOrdersHandler liveOrdersHandler;
    private OrdersHandler ordersHandler;
    private TradeReportsHandler tradeReportsHandler;

    private Messenger messenger;

    private Ticker requestTicker = null;
    private Ticker orderTicker = null;

    private ConnectionController connectionController;

    public MainController(Messenger messenger) {

        IllegalArgumentsValidator.checkNullPointerPassed(messenger);

        this.messenger = messenger;
    }

    public boolean isConnected() {

        if (requestTicker != null && orderTicker != null
                && connectionController != null
                && connectionController.isConnected()) {
            return true;

        }
        return false;
    }

    public void displayNotConnected() {
        this.messenger.show("Not connected!");
    }

    public TradeReportsHandler getTradeReportsHandler() {
        return tradeReportsHandler;
    }

    public void setTradeReportsHandler(TradeReportsHandler tradeReportsHandler) {
        this.tradeReportsHandler = tradeReportsHandler;
    }

    public LiveOrdersHandler getLiveOrdersHandler() {
        return liveOrdersHandler;
    }

    public void setLiveOrdersHandler(LiveOrdersHandler liveOrdersHandler) {
        this.liveOrdersHandler = liveOrdersHandler;
    }

    public OrdersHandler getOrdersHandler() {
        return ordersHandler;
    }

    public void setOrdersHandler(OrdersHandler ordersHandler) {
        this.ordersHandler = ordersHandler;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public void setConnectionController(
            ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    private int initializeTickers(int nextValidOrderId) {

        if (orderTicker != null || requestTicker != null) {
            // throw new Exception("Tickers area already initialized!");
            return 0;
        }

        this.orderTicker = new Ticker(nextValidOrderId);

        // let order id's not collide with other request id's
        // because request and order id's use the same id's pool
        this.requestTicker = new Ticker(orderTicker.getTickerId() + 10000000);

        return 1;

    }

    public Messenger getMessenger() {
        return messenger;
    }

    public Ticker getRequestTicker() {
        return requestTicker;
    }

    public Ticker getOrderTicker() {
        return orderTicker;
    }

    public TopDataResultRowsHandler getTopDataResultRowsHandler() {
        return topDataResultRowsHandler;
    }

    public void setTopDataResultRowsHandler(
            TopDataResultRowsHandler topDataResultRowsHandler) {
        this.topDataResultRowsHandler = topDataResultRowsHandler;
    }

    public HistoricalDataResultPanelsHandler getHistoricalDataHandler() {
        return historicalDataHandler;
    }

    public void setHistoricalDataHandler(
            HistoricalDataResultPanelsHandler historicalDataHandler) {
        this.historicalDataHandler = historicalDataHandler;
    }

    public RealTimeBarsDataResultPanelsHandler getRealTimeBarsHandler() {
        return realTimeBarsHandler;
    }

    public void setRealTimeBarsHandler(
            RealTimeBarsDataResultPanelsHandler realTimeBarsHandler) {
        this.realTimeBarsHandler = realTimeBarsHandler;
    }

    @Override
    public void error(Exception e) {
        messenger.show("TWS exception: " + e.toString());
    }

    @Override
    public void error(String str) {
        messenger.show("TWS error: " + str);

    }

    @Override
    public void error(int id, int errorCode, String errorMsg) {
        messenger.show("TWS error: id=" + id + ", error code=" + errorCode
                + ", errorMsg=" + errorMsg);

    }

    /*
     * ==================CONNECTION TO SERVER===========================
     */
    @Override
    public void connectionClosed() {
        messenger.show("TWS: Connection closed.");
    }

    @Override
    public void updateAccountValue(String key, String value, String currency,
            String accountName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updatePortfolio(Contract contract, int position,
            double marketPrice, double marketValue, double averageCost,
            double unrealizedPNL, double realizedPNL, String accountName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateAccountTime(String timeStamp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void accountDownloadEnd(String accountName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation,
            int side, double price, int size) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateMktDepthL2(int tickerId, int position,
            String marketMaker, int operation, int side, double price, int size) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message,
            String origExchange) {
        // TODO Auto-generated method stub

    }

    @Override
    public void managedAccounts(String accountsList) {

        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(accountsList, ",");

        while (st.hasMoreTokens()) {

            String accountName = st.nextToken();
            list.add(accountName);
        }

        this.connectionController.accountList(list);
    }

    @Override
    public void receiveFA(int faDataType, String xml) {
        // TODO Auto-generated method stub

    }

    /*
     * =======================HISTORICAL DATA==========================
     */
    @Override
    public void historicalData(int tickerId, String date, double open,
            double high, double low, double close, int volume, int count,
            double wap, boolean hasGaps) {

        IHistoricalDataHandler handler = this.historicalDataHandler
                .getIHistoricalDataHandler(tickerId);

        if (handler != null) {
            if (date.startsWith("finished")) {
                handler.historicalDataEnd();
            } else {
                long longDate;
                if (date.length() == 8) {
                    int year = Integer.parseInt(date.substring(0, 4));
                    int month = Integer.parseInt(date.substring(4, 6));
                    int day = Integer.parseInt(date.substring(6));
                    longDate = new Date(year - 1900, month - 1, day).getTime() / 1000;
                } else {
                    longDate = Long.parseLong(date);
                }
                Bar bar = new Bar(longDate, high, low, open, close, wap,
                        volume, count);
                handler.historicalData(bar, hasGaps);
            }
        }
    }

    @Override
    public void scannerParameters(String xml) {
        // TODO Auto-generated method stub

    }

    @Override
    public void scannerData(int reqId, int rank,
            ContractDetails contractDetails, String distance, String benchmark,
            String projection, String legsStr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void scannerDataEnd(int reqId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high,
            double low, double close, long volume, double wap, int count) {
        // TODO Auto-generated method stub

    }

    @Override
    public void currentTime(long time) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fundamentalData(int reqId, String data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deltaNeutralValidation(int reqId, UnderComp underComp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void position(String account, Contract contract, int pos,
            double avgCost) {
        // TODO Auto-generated method stub

    }

    @Override
    public void positionEnd() {
        // TODO Auto-generated method stub

    }

    @Override
    public void accountSummary(int reqId, String account, String tag,
            String value, String currency) {
        // TODO Auto-generated method stub

    }

    @Override
    public void accountSummaryEnd(int reqId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void tickOptionComputation(int tickerId, int field,
            double impliedVol, double delta, double optPrice,
            double pvDividend, double gamma, double vega, double theta,
            double undPrice) {
        // TODO Auto-generated method stub

    }

    /*
     * =======================TOP MARKET DATA================
     */
    @Override
    public void tickPrice(int reqId, int tickType, double price,
            int canAutoExecute) {
        ITopMktDataHandler handler = topDataResultRowsHandler
                .getITopMktDataHandler(reqId);
        if (handler != null) {
            handler.tickPrice(NewTickType.get(tickType), price, canAutoExecute);
        }
    }

    @Override
    public void tickGeneric(int reqId, int tickType, double value) {
        ITopMktDataHandler handler = topDataResultRowsHandler
                .getITopMktDataHandler(reqId);
        if (handler != null) {
            handler.tickPrice(NewTickType.get(tickType), value, 0);
        }
    }

    @Override
    public void tickSize(int reqId, int tickType, int size) {
        ITopMktDataHandler handler = topDataResultRowsHandler
                .getITopMktDataHandler(reqId);
        if (handler != null) {
            handler.tickSize(NewTickType.get(tickType), size);
        }
    }

    @Override
    public void tickString(int reqId, int tickType, String value) {
        ITopMktDataHandler handler = topDataResultRowsHandler
                .getITopMktDataHandler(reqId);
        if (handler != null) {
            handler.tickString(NewTickType.get(tickType), value);
        }
    }

    @Override
    public void tickEFP(int reqId, int tickType, double basisPoints,
            String formattedBasisPoints, double impliedFuture, int holdDays,
            String futureExpiry, double dividendImpact, double dividendsToExpiry) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void tickSnapshotEnd(int reqId) {

        throw new UnsupportedOperationException();

    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {

        ITopMktDataHandler handler = topDataResultRowsHandler
                .getITopMktDataHandler(reqId);

        if (handler != null) {
            handler.marketDataType(MktDataType.get(marketDataType));
        }
    }

    /*
     * ==================ORDERS===========================
     */

    /*
     * ==================LIVE ORDERS===========================
     */

    /**
     * this method is called after successful connection to TWS
     */
    @Override
    public void nextValidId(int orderId) {

        initializeTickers(orderId);
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order orderIn,
            OrderState orderState) {
        NewOrder order = new NewOrder(orderIn);

        IOrderHandler handler = ordersHandler
                .getIOrdersHandlerByTickerId(orderId);

        if (handler != null) {

            handler.orderState(new NewOrderState(orderState));
        }

        if (!order.whatIf()) {

            for (ILiveOrderHandler liveHandler : liveOrdersHandler
                    .getLiveOrderHandlersMap()) {

                liveHandler.openOrder(new NewContract(contract), order,
                        new NewOrderState(orderState));
            }
        }
    }

    @Override
    public void openOrderEnd() {

        for (ILiveOrderHandler handler : liveOrdersHandler
                .getLiveOrderHandlersMap()) {

            handler.openOrderEnd();
        }
    }

    @Override
    public void orderStatus(int orderId, String status, int filled,
            int remaining, double avgFillPrice, int permId, int parentId,
            double lastFillPrice, int clientId, String whyHeld) {

        for (ILiveOrderHandler handler : liveOrdersHandler
                .getLiveOrderHandlersMap()) {

            handler.orderStatus(orderId, OrderStatus.valueOf(status), filled,
                    remaining, avgFillPrice, permId, parentId, lastFillPrice,
                    clientId, whyHeld);
        }
    }

    /*
     * ==================TRADE REPORTS===========================
     */
    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {

        ITradeReportHandler iTradeReportHandler = tradeReportsHandler
                .getiTradeReportHandler();

        if (iTradeReportHandler != null) {

            int i = execution.m_execId.lastIndexOf('.');
            String tradeKey = execution.m_execId.substring(0, i);
            iTradeReportHandler.tradeReport(tradeKey,
                    new NewContract(contract), execution);
        }
    }

    @Override
    public void execDetailsEnd(int reqId) {

        ITradeReportHandler iTradeReportHandler = tradeReportsHandler
                .getiTradeReportHandler();
        if (iTradeReportHandler != null) {
            iTradeReportHandler.tradeReportEnd();
        }
    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {

        ITradeReportHandler iTradeReportHandler = tradeReportsHandler
                .getiTradeReportHandler();

        if (iTradeReportHandler != null) {

            int i = commissionReport.m_execId.lastIndexOf('.');
            String tradeKey = commissionReport.m_execId.substring(0, i);
            iTradeReportHandler.commissionReport(tradeKey, commissionReport);
        }
    }

    // ==============================

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        // TODO Auto-generated method stub

    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        // TODO Auto-generated method stub

    }

    @Override
    public void contractDetailsEnd(int reqId) {
        // TODO Auto-generated method stub

    }
}
