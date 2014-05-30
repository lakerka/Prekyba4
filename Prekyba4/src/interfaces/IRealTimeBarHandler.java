package interfaces;

import com.ib.controller.Bar;

public interface IRealTimeBarHandler {
    void realtimeBar(Bar bar); // time is in seconds since epoch
}
