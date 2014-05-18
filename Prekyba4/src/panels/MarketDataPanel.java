package panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import model.Ticker;

import com.ib.controller.NewContract;

import controllers.ConnectionController;
import controllers.MainController;

public class MarketDataPanel extends JPanel {

    private final NewContract m_contract = new NewContract();
    private final NewTabbedPanel requestsPanel = new NewTabbedPanel();
    private final NewTabbedPanel resultsPanel = new NewTabbedPanel();
//    private ConnectionController connectionController;
//    private Ticker ticker;

    // private TopResultsPanel m_topResultPanel;

    public MarketDataPanel(ConnectionController connectionController,
            Ticker ticker, MainController mainController) {

//        this.connectionController = connectionController;
//        this.ticker = ticker;

        requestsPanel.addTab("Historical Data", new HistRequestPanel(
                connectionController, resultsPanel, ticker, mainController));

        setLayout(new BorderLayout());
        add(requestsPanel, BorderLayout.NORTH);
        add(resultsPanel);
    }

}
