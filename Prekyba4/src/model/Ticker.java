package model;

public class Ticker {

    int tickerId;

    public Ticker() {
        this.tickerId = -1;
    }

    public Ticker(int tickerId) {

        setTickerId(tickerId);
    }

    synchronized private int setTickerId(int tickerId) {

        if (tickerId < 0) {
            throw new IllegalArgumentException("Arguments must be positive!");
        }

        this.tickerId = tickerId;

        return 1;
    }

    synchronized public int getNewTickerId() {

        if (Integer.MAX_VALUE == tickerId) {
            throw new RuntimeException("Next tickerId is too large to fit int!");
        }

        tickerId++;

        return tickerId;
    }
    
    synchronized public int getTickerId() {

        return tickerId;
    }

}
