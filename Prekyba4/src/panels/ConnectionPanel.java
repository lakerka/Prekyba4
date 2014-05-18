package panels;

import initial.EClientSocketObserved;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import listeners.ConnectionStatusListener;

import com.ib.client.EClientSocket;

import controllers.ConnectionController;

public class ConnectionPanel extends JPanel implements ConnectionStatusListener {

    private final JTextField m_host = new JTextField("127.0.0.1", 10);
    private final JTextField m_port = new JTextField("7496", 10);
    private final JTextField m_clientId = new JTextField("0", 10);
    private final JLabel connectionStatusJLabel = new JLabel("Disconnected");

    private ConnectionController connectionController;
    private Messenger messenger;

    public ConnectionPanel(final ConnectionController connectionController,
            Messenger messenger) {

        if (connectionController == null || messenger == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }

        this.connectionController = connectionController;
        this.connectionController.registerListener(this);
        this.messenger = messenger;

        JButton connect = new JButton("Connect");
        connect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                onConnect();

            }
        });

        JButton disconnect = new JButton("Disconnect");

        disconnect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                onDisconnect();
            }
        });

        JPanel p1 = new VerticalPanel();
        p1.add("Host", m_host);
        p1.add("Port", m_port);
        p1.add("Client ID", m_clientId);

        JPanel p2 = new VerticalPanel();
        p2.add(connect);
        p2.add(disconnect);
        p2.add(Box.createVerticalStrut(20));

        JPanel p3 = new VerticalPanel();
        p3.setBorder(new EmptyBorder(20, 0, 0, 0));
        p3.add("Connection status: ", connectionStatusJLabel);

        JPanel p4 = new JPanel(new BorderLayout());
        p4.add(p1, BorderLayout.WEST);
        p4.add(p2);
        p4.add(p3, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(p4, BorderLayout.NORTH);
        
    }

    protected void onConnect() {
        
        int port = Integer.parseInt(m_port.getText());
        int clientId = Integer.parseInt(m_clientId.getText());
        String host = m_host.getText();
        
        if (host == null || host.isEmpty()) {
            
            messenger.show("Host is empty!");
            return;
        }

        connectionController.eConnect(host, port, clientId);
    }

    protected void onDisconnect() {

        connectionController.eDisconnect();
    }

    @Override
    public void connectionStatusChanged(boolean isConnected) {
        
        if (isConnected) {
            
            connectionStatusJLabel.setText("Connected");
            
        }else {
            
            connectionStatusJLabel.setText("Disconnected");
            
        }
        
    }

}
