package handlers;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.controller.ConcurrentHashSet;
import com.ib.controller.NewContract;
import com.ib.controller.NewOrder;
import com.ib.controller.NewOrderState;
import com.ib.controller.OrderStatus;
import com.ib.controller.Types.ExerciseType;

import initial.IllegalArgumentsValidator;
import interfaces.ILiveOrderHandler;
import interfaces.IOrderHandler;
import model.Ticker;
import controllers.ConnectionController;
import controllers.MainController;

public class LiveOrdersHandler {

    private ConnectionController connectionController;
    private MainController mainController;
    private Ticker ticker;

    private final ConcurrentHashSet<ILiveOrderHandler> liveOrderHandlersMap = new ConcurrentHashSet<ILiveOrderHandler>();

    
    public LiveOrdersHandler(ConnectionController connectionController,
            MainController mainController) {

        IllegalArgumentsValidator.checkNullPointerPassed(connectionController,
                mainController);

        try {

            this.connectionController = connectionController;
            this.mainController = mainController;
            this.ticker = mainController.getRequestTicker();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void cancelOrder(int orderId) {
        
        connectionController.cancelOrder(orderId);
    }

    public void cancelAllOrders() {
        
        connectionController.reqGlobalCancel();
    }

    // put into effect the right specified in a contract
    public void exerciseOption(String account, NewContract contract,
            ExerciseType type, int quantity, boolean override) {

        connectionController.exerciseOptions(ticker.getNewTickerId(), contract.getContract(),
                type.ordinal(), quantity, account, override ? 1 : 0);
    }

    public ConcurrentHashSet<ILiveOrderHandler> getLiveOrderHandlersMap() {
        return liveOrderHandlersMap;
    }


    public void reqLiveOrders(ILiveOrderHandler handler) {
        
        liveOrderHandlersMap.add(handler);
        connectionController.reqAllOpenOrders();
    }

    public void takeTwsOrders(ILiveOrderHandler handler) {
        
        liveOrderHandlersMap.add(handler);
        connectionController.reqOpenOrders();
    }

    public void takeFutureTwsOrders(ILiveOrderHandler handler) {
        
        liveOrderHandlersMap.add(handler);
        connectionController.reqAutoOpenOrders(true);
    }

    public void removeLiveOrderHandler(ILiveOrderHandler handler) {
        
        liveOrderHandlersMap.remove(handler);
    }

    

}
