package controllers;

import initial.EClientSocketObserved;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.AlreadyConnectedException;

import com.ib.client.AnyWrapper;
import com.ib.client.ComboLeg;
import com.ib.client.Contract;
import com.ib.client.EClientErrors;
import com.ib.client.EClientSocket;
import com.ib.controller.NewContract;
import com.ib.controller.NewOrder;

public class ConnectionController extends EClientSocketObserved {

    public ConnectionController(AnyWrapper anyWrapper,
            int connectivityLookupDelayInMiliseconds) {

        super(anyWrapper, connectivityLookupDelayInMiliseconds);

    }

    public synchronized void placeOrder(NewContract contract, NewOrder order) {
       
    }
    
    

}
