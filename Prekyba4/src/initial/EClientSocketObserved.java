package initial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import listeners.ConnectionStatusListener;

import com.ib.client.AnyWrapper;
import com.ib.client.EClientSocket;

public class EClientSocketObserved extends EClientSocket {

    private Collection<ConnectionStatusListener> connectionStatusListenerCollection;
    private final ConnectionObserver connectionObserver;

    public EClientSocketObserved(AnyWrapper anyWrapper, int periodInMiliseconds) {
        super(anyWrapper);

        if (periodInMiliseconds <= 0) {

            throw new IllegalArgumentException(
                    "periodInMiliseconds must be greater than zero!");
        }

        connectionStatusListenerCollection = new ArrayList<ConnectionStatusListener>();
        this.connectionObserver = new ConnectionObserver(super.isConnected(),
                this);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(connectionObserver, 0,
                periodInMiliseconds, TimeUnit.MILLISECONDS);

    }

    public void connectionStatusChanged(boolean connectionStatusChangedTo) {

        notifyListeners(connectionStatusChangedTo);
    }

    public int registerListener(
            ConnectionStatusListener connectionStatusListener) {

        return connectionStatusListenerCollection.add(connectionStatusListener) == true ? 1
                : 0;
    }

    public int unregisterListener(

    ConnectionStatusListener connectionStatusListener) {
        return connectionStatusListenerCollection
                .remove(connectionStatusListener) == true ? 1 : 0;
    }

    private int notifyListeners(boolean isConnected) {

        try {

            for (ConnectionStatusListener connectionStatusListener : connectionStatusListenerCollection) {

                connectionStatusListener.connectionStatusChanged(isConnected);
            }

            return 1;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0;
    }

}
