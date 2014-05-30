package handlers;

import initial.IllegalArgumentsValidator;
import initial.Messenger;
import interfaces.ITradeReportHandler;
import model.Ticker;

import com.ib.client.ExecutionFilter;

import controllers.ConnectionController;
import controllers.MainController;

public class TradeReportsHandler {

    private Messenger messenger;
    private MainController mainController;
    private ITradeReportHandler iTradeReportHandler;

    public TradeReportsHandler(MainController mainController) {

        IllegalArgumentsValidator.checkNullPointerPassed(mainController);

        try {
            this.mainController = mainController;
            this.messenger = mainController.getMessenger();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    /**
     * sets iTradeReportHandler and send sends request of executions of orders
     * to server
     */
    public void reqExecutions(ExecutionFilter filter,
            ITradeReportHandler handler) {

        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }
        
        iTradeReportHandler = handler;
        mainController.getConnectionController().reqExecutions(
                mainController.getRequestTicker().getNewTickerId(), filter);
    }

    public ITradeReportHandler getiTradeReportHandler() {
        return iTradeReportHandler;
    }

    public void setiTradeReportHandler(ITradeReportHandler iTradeReportHandler) {
        this.iTradeReportHandler = iTradeReportHandler;
    }

}
