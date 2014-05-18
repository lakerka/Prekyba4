package initial;
import com.ib.client.EClientSocket;


public class ConnectionObserver implements Runnable {

    private boolean isConnected;
    private final EClientSocketObserved eClientSocketObserved;
    
    public ConnectionObserver(boolean connectionState, EClientSocketObserved eClientSocketObserved) {
        
        if (eClientSocketObserved == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        
        this.isConnected = connectionState;
        this.eClientSocketObserved = eClientSocketObserved;
    }

    @Override
    public void run() {
        
        boolean curIsConnected = eClientSocketObserved.isConnected();
        
        if (curIsConnected != isConnected) {
            
            isConnected = curIsConnected;
            eClientSocketObserved.connectionStatusChanged(curIsConnected);
        }
        
    }

}
