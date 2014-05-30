package handlers;

import initial.IllegalArgumentsValidator;
import interfaces.IHistoricalDataHandler;
import interfaces.ILiveOrderHandler;
import interfaces.IOrderHandler;

import java.util.HashMap;

import model.Ticker;
import utilities.Support;

import com.ib.controller.ConcurrentHashSet;
import com.ib.controller.NewContract;
import com.ib.controller.NewOrder;
import com.ib.controller.Types.ExerciseType;

import controllers.ConnectionController;
import controllers.MainController;

public class OrdersHandler {

    private final HashMap<Integer, IOrderHandler> ordersHandlerMap = new HashMap<Integer, IOrderHandler>();
   
    private MainController mainController;

    public OrdersHandler(ConnectionController connectionController, MainController mainController) {

        IllegalArgumentsValidator.checkNullPointerPassed(connectionController, mainController);
        
        try {

            this.mainController = mainController;

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
    
    public void placeOrModifyOrder(NewContract contract, final NewOrder order,
            final IOrderHandler handler) {
        
        // when placing new order, assign new order id
        if (order.orderId() == 0) {
            
            order.orderId(mainController.getOrderTicker().getNewTickerId());
            
            if (handler != null) {
            
                ordersHandlerMap.put(order.orderId(), handler);
            }
        }

        mainController.getConnectionController().placeOrder(contract, order);
    }

    public void removeOrderHandler(IOrderHandler handler) {
        
        Support.getAndRemoveKey(ordersHandlerMap, handler);
    }
    
    //get by tickerId
    public IOrderHandler getIOrdersHandlerByTickerId(int tickerId) {

        return this.ordersHandlerMap.get(tickerId);
        
    }

}
