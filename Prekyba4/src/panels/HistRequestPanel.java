package panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import handlers.HistoricalDataResultPanelsHandler;
import handlers.RealTimeBarsDataResultPanelsHandler;
import initial.TCombo;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Ticker;

import com.ib.controller.NewContract;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Types.WhatToShow;

import controllers.ConnectionController;
import controllers.MainController;
import initial.*;

/*
 Panel that must be under market data tab
 where you can choose required parameters for
 getting history data from TWS
 */

public class HistRequestPanel extends JPanel {

    // final ContractPanel m_contractPanel = new ContractPanel(m_contract);
    final JTextFieldNumbersSupported m_end = new JTextFieldNumbersSupported();

    final JTextFieldNumbersSupported m_duration = new JTextFieldNumbersSupported();

    final TCombo<DurationUnit> m_durationUnit = new TCombo<DurationUnit>(
            DurationUnit.values());

    final TCombo<BarSize> m_barSize = new TCombo<BarSize>(BarSize.values());

    final TCombo<WhatToShow> m_whatToShow = new TCombo<WhatToShow>(
            WhatToShow.values());

    final JCheckBox m_rthOnly = new JCheckBox();

    final ContractPanel contractPanel;

    private ConnectionController connectionController;
    private final NewContract newContract = new NewContract();
    private NewTabbedPanel resultsPanel;
    private Ticker ticker;
    private MainController mainController;

    public HistRequestPanel(ConnectionController connectionController,
            NewTabbedPanel resultsPanel, Ticker ticker,
            MainController mainController) {

        if (connectionController == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }

        this.contractPanel = new ContractPanel(this.newContract);

        this.connectionController = connectionController;
        this.resultsPanel = resultsPanel;
        this.ticker = ticker;
        this.mainController = mainController;

        // initialize panel with example data
        m_end.setText("20130101 12:00:00");
        m_duration.setText("1");
        m_durationUnit.setSelectedItem(DurationUnit.WEEK);
        m_barSize.setSelectedItem(BarSize._1_hour);

        JButton button = new JButton("Request historical data");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                onHistorical();

            }
        });

        VerticalPanel paramPanel = new VerticalPanel();

        paramPanel.add("End", m_end);
        paramPanel.add("Duration", m_duration);
        paramPanel.add("Duration unit", m_durationUnit);
        paramPanel.add("Bar size", m_barSize);
        paramPanel.add("What to show", m_whatToShow);
        paramPanel.add("RTH only", m_rthOnly);

        VerticalPanel butPanel = new VerticalPanel();
        butPanel.add(button);

        JPanel rightPanel = new StackPanel();
        rightPanel.add(paramPanel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(butPanel);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(contractPanel);
        add(Box.createHorizontalStrut(20));
        add(rightPanel);
    }

    protected void onHistorical() {

        contractPanel.onOK();

        TableResultPanel panel = new TableResultPanel(true,
                mainController.getHistoricalDataHandler(),
                mainController.getRealTimeBarsHandler());

        mainController.getHistoricalDataHandler().reqHistoricalData(ticker,
                newContract, m_end.getText(), m_duration.getInt(),
                m_durationUnit.getSelectedItem(), m_barSize.getSelectedItem(),
                m_whatToShow.getSelectedItem(), m_rthOnly.isSelected(), panel);

        resultsPanel.addTab("Historical " + newContract.symbol(), panel, true,
                true);
    }
}