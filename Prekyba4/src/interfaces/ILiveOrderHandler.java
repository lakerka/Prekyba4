package interfaces;

import com.ib.controller.NewContract;
import com.ib.controller.NewOrder;
import com.ib.controller.NewOrderState;
import com.ib.controller.OrderStatus;

public interface ILiveOrderHandler {

    void openOrder(NewContract contract, NewOrder order,
            NewOrderState orderState);

    void openOrderEnd();

    /**
     * function that handles data received from server about all open orders
     */

    void orderStatus(int orderId, OrderStatus status, int filled,
            int remaining, double avgFillPrice, long permId, int parentId,
            double lastFillPrice, int clientId, String whyHeld);

    void handle(int orderId, int errorCode, String errorMsg); // add permId?
}