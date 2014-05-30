package panels;

import initial.IllegalArgumentsValidator;
import handlers.TopDataResultRowsHandler;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.Ticker;
import buttons.HtmlButton;

import com.ib.controller.NewContract;

import controllers.ConnectionController;
import controllers.MainController;

/*
 Panel used for requesting top (realtime) data
 */

public class TopRequestPanel extends JPanel {

    private final NewContract newContract = new NewContract();

    // result panel where view of data received will be displayed
    private NewTabbedPanel resultsPanel;

    // Result table panel where will be table of data
    private TopResultsTablePanel topResultsTablePanel;

    private MainController mainController;
    final ContractPanel m_contractPanel = new ContractPanel(newContract);

    /**
     * 
     */
    private static final long serialVersionUID = -100206204849808528L;

    public TopRequestPanel(MainController mainController,
            NewTabbedPanel resultsPanel) {

        IllegalArgumentsValidator.checkNullPointerPassed(mainController,
                resultsPanel);

        this.mainController = mainController;
        this.resultsPanel = resultsPanel;

        HtmlButton reqTop = new HtmlButton("Request Top Market Data") {
            @Override
            protected void actionPerformed() {
                onTop();
            }
        };

        VerticalPanel butPanel = new VerticalPanel();
        butPanel.add(reqTop);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(m_contractPanel);
        add(Box.createHorizontalStrut(20));
        add(butPanel);
    }

    protected void onTop() {

        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }
        
        m_contractPanel.onOK();

        if (topResultsTablePanel == null) {

            topResultsTablePanel = new TopResultsTablePanel(
                    mainController.getTopDataResultRowsHandler(), this);

            resultsPanel.addTab("Top Data", topResultsTablePanel, true, true);
        }

        topResultsTablePanel.addRowToTopModel(newContract);
    }

    public void destroyTopResultsTablePanel() {
        topResultsTablePanel = null;
    }
}