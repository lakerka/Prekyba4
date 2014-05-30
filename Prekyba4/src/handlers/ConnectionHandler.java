package handlers;

import interfaces.IConnectionHandler;
import interfaces.ILiveOrderHandler;

import java.util.ArrayList;

import controllers.ConnectionController;


public class ConnectionHandler implements IConnectionHandler {

    private ConnectionController connectionController;
    
    private final ArrayList<String> accountList = new ArrayList<String>();
    
    
    public ConnectionHandler(ConnectionController connectionController) {

        if (connectionController == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        
        try {

            this.connectionController = connectionController;

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
    

    public ArrayList<String> accountList()  { return accountList; }


    @Override
    public void connected() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void disconnected() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void accountList(ArrayList<String> list) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void error(Exception e) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void message(int id, int errorCode, String errorMsg) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void show(String string) {
        // TODO Auto-generated method stub
        
    }
 

}
