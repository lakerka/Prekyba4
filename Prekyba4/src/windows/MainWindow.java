package windows;

import handlers.HistoricalDataResultPanelsHandler;
import handlers.LiveOrdersHandler;
import handlers.OrdersHandler;
import handlers.RealTimeBarsDataResultPanelsHandler;
import handlers.TopDataResultRowsHandler;
import handlers.TradeReportsHandler;
import initial.Messenger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

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
import panels.NewTabbedPanel;
import panels.TradingPanel;

public class MainWindow extends JFrame {

    private MainController mainController;
    private ConnectionController connectionController;
    private Messenger messenger;
    private JTextArea messageTextArea;
    private Ticker requestTicker;
    private Ticker orderTicker;

    public MainWindow() {

        // initialize messenger
        this.messageTextArea = new JTextArea();
        this.messenger = new Messenger(messageTextArea);

        this.mainController = new MainController(messenger);

        this.connectionController = new ConnectionController(mainController,
                2000);

        this.mainController.setConnectionController(connectionController);

        this.mainController
                .setHistoricalDataHandler(new HistoricalDataResultPanelsHandler(
                        mainController));
        this.mainController
                .setRealTimeBarsHandler(new RealTimeBarsDataResultPanelsHandler(
                        connectionController));
        this.mainController
                .setTopDataResultRowsHandler(new TopDataResultRowsHandler(
                        mainController));

        this.mainController.setOrdersHandler(new OrdersHandler(
                connectionController, this.mainController));

        this.mainController.setLiveOrdersHandler(new LiveOrdersHandler(
                connectionController, this.mainController));

        this.mainController.setTradeReportsHandler(new TradeReportsHandler(
                this.mainController));

    }

    public void init() {

        // Create and set up the window.
        setTitle("Prekyba");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 700));

        NewTabbedPanel jTabbedPane = new NewTabbedPanel(true);
        this.add(jTabbedPane);

        // create content of this jframe
        JComponent jComponent1 = new ConnectionPanel(connectionController,
                messenger);
        jTabbedPane.addTab("Connections", jComponent1);

        JComponent jComponent2 = new MarketDataPanel(connectionController,
                requestTicker, mainController);
        jTabbedPane.addTab("Market data", jComponent2);

        JComponent jComponent3 = new TradingPanel(this, mainController);
        jTabbedPane.addTab("Trading", jComponent3);

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
