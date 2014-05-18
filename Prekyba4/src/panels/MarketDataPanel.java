package panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import model.Ticker;

import com.ib.controller.NewContract;

import controllers.ConnectionController;
import controllers.MainController;

/*
 panel used to display various types of market data
 */
public class MarketDataPanel extends JPanel {

    private final NewContract m_contract = new NewContract();
    private final NewTabbedPanel requestsPanel = new NewTabbedPanel();
    private final NewTabbedPanel resultsPanel = new NewTabbedPanel();

    public MarketDataPanel(ConnectionController connectionController,
            Ticker ticker, MainController mainController) {

        requestsPanel.addTab("Historical", new HistRequestPanel(
                connectionController, resultsPanel, ticker, mainController));

        requestsPanel.addTab("Realtime", new TopRequestPanel(mainController, resultsPanel));

        setLayout(new BorderLayout());
        add(requestsPanel, BorderLayout.NORTH);
        add(resultsPanel);
    }

}
