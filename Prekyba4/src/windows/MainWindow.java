package windows;

import handlers.HistoricalDataResultPanelsHandler;
import handlers.RealTimeBarsDataResultPanelsHandler;
import handlers.TopDataResultRowsHandler;
import initial.Messenger;

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

import model.Ticker;
import controllers.ConnectionController;
import controllers.MainController;
import panels.ConnectionPanel;
import panels.MarketDataPanel;

public class MainWindow extends JFrame {

    private MainController mainController;
    private ConnectionController connectionController;
    private Messenger messenger;
    private JTextArea messageTextArea;
    private Ticker ticker;

    public MainWindow() {

        // initialize messenger
        this.messageTextArea = new JTextArea();
        this.messenger = new Messenger(messageTextArea);

        this.ticker = new Ticker();
        this.mainController = new MainController(messenger, ticker);
        
        this.connectionController = new ConnectionController(mainController,
                2000);
        
        this.mainController
                .setHistoricalDataHandler(new HistoricalDataResultPanelsHandler(
                        connectionController));
        this.mainController
                .setRealTimeBarsHandler(new RealTimeBarsDataResultPanelsHandler(
                        connectionController));
        this.mainController
                .setTopDataResultRowsHandler(new TopDataResultRowsHandler(
                        connectionController, mainController));

    }

    public void init() {

        // Create and set up the window.
        setTitle("Prekyba");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 700));

        JTabbedPane jTabbedPane = new JTabbedPane();
        this.add(jTabbedPane);

        // create content of this jframe
        JComponent jComponent1 = new ConnectionPanel(connectionController,
                messenger);
        jTabbedPane.addTab("Connections", null, jComponent1, null);

        JComponent jComponent2 = new MarketDataPanel(connectionController,
                ticker, mainController);
        jTabbedPane.addTab("Market data", null, jComponent2, null);

        JComponent jComponent3 = makeTextPanel("Trading");
        jTabbedPane.addTab("Trading", null, jComponent3, null);

        messageTextArea.setEditable(false);
        messageTextArea.setLineWrap(true);

        JScrollPane msgScroll = new JScrollPane(messageTextArea);
        msgScroll.setPreferredSize(new Dimension(10000, 200));

        JTabbedPane messagePane = new JTabbedPane();
        messagePane.addTab("Messages", msgScroll);

        this.add(messagePane, BorderLayout.SOUTH);

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
