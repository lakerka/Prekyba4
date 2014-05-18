package listeners;

public interface ConnectionStatusListener {

    //1 connected
    //0 disconnected
    public void connectionStatusChanged(boolean connectionStatus);
}
