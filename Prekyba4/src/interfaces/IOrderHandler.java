package interfaces;

import com.ib.controller.NewOrderState;

public interface IOrderHandler {
    
    /**
     * method used to pass data from EWrapper to some model
     */
    void orderState(NewOrderState orderState);

    void handle(int errorCode, String errorMsg);
}