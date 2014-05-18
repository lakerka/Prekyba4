package windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import controllers.ConnectionController;
import controllers.MainController;
import panels.ConnectionPanel;
import panels.Messenger;

public class MainWindow extends JFrame {

    private MainController mainController;
    private ConnectionController connectionController;
    private Messenger messenger;
    private JTextArea messageTextArea;
    
    
    
    public MainWindow() {
        
        this.mainController = new MainController();
        this.connectionController = new ConnectionController(mainController, 2000);
        
        //initialize messenger
        this.messageTextArea = new JTextArea();
        this.messenger = new Messenger(messageTextArea);
    }
    
    public void init() {
        
        //Create and set up the window.
        setTitle("Prekyba");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 500));
        
        JTabbedPane jTabbedPane = new JTabbedPane();
        this.add(jTabbedPane);
        
        //create socket
        
//        EClientSocketObserved eClientSocketObserved = new EClientSocketObserved(null, 3000);
        
        //create content of this jframe
        JComponent jComponent1 = new ConnectionPanel(connectionController, messenger);
        jTabbedPane.addTab("Prisijungimas", null, jComponent1, "Skirta prisijungimui prie serverio");
        
        JComponent jComponent2 = makeTextPanel("Rinkos duomenys");
        jTabbedPane.addTab("Rinkos duomenys", null, jComponent2, "Skirta gauti rinkos duomenims");
        
        JComponent jComponent3 = makeTextPanel("Sandoriu siuntimas");
        jTabbedPane.addTab("Sandoriu siuntimas", null, jComponent3, "Skirta siusti sandoriams");
        
       
        
        messageTextArea.setEditable( false);
        messageTextArea.setLineWrap( true);
        
        JScrollPane msgScroll = new JScrollPane( messageTextArea );
        msgScroll.setPreferredSize( new Dimension( 10000, 200) );
        
        JTabbedPane messagePane = new JTabbedPane();
        messagePane.addTab( "Messages", msgScroll);
        
        
        this.add( messagePane, BorderLayout.SOUTH);
        
        pack();
        setVisible(true);
    }
    
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
     


}
