package panels;

import java.awt.Component;
import java.awt.TextField;
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
    final JTextFieldNumbersSupported endDate = new JTextFieldNumbersSupported();

    final JTextFieldNumbersSupported duration = new JTextFieldNumbersSupported();

    final TCombo<DurationUnit> durationUnit = new TCombo<DurationUnit>(
            DurationUnit.values());

    final TCombo<BarSize> barSize = new TCombo<BarSize>(BarSize.values());

    final TCombo<WhatToShow> whatToShow = new TCombo<WhatToShow>(
            WhatToShow.values());

    final JCheckBox regularTradingHoursOnly = new JCheckBox();

    final ContractPanel contractPanel;

    private final NewContract newContract = new NewContract();
    private NewTabbedPanel resultsPanel;
    private Ticker ticker;
    private MainController mainController;

    public HistRequestPanel(NewTabbedPanel resultsPanel,
            MainController mainController) {

        if (mainController == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }

        this.contractPanel = new ContractPanel(this.newContract);

        this.resultsPanel = resultsPanel;
        this.mainController = mainController;

        // initialize panel with example data
        String endDateString = "20130101 12:00:00";
        endDate.setColumns(endDateString.length() / 2 + 2);
        endDate.setText(endDateString);

        duration.setText("1");
        durationUnit.setSelectedItem(DurationUnit.WEEK);
        barSize.setSelectedItem(BarSize._1_hour);

        JButton button = new JButton("Request historical data");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                onHistorical();

            }
        });

        VerticalPanel paramPanel = new VerticalPanel();

        paramPanel.add("End", endDate);
        paramPanel.add("Duration", duration);
        paramPanel.add("Duration unit", durationUnit);
        paramPanel.add("Bar size", barSize);
        paramPanel.add("What to show", whatToShow);
        paramPanel.add("Regular trading hours", regularTradingHoursOnly);

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
        
        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }

        TableResultPanel panel = new TableResultPanel(true,
                mainController.getHistoricalDataHandler(),
                mainController.getRealTimeBarsHandler());

        mainController.getHistoricalDataHandler().reqHistoricalData(
                newContract, endDate.getText(), duration.getInt(),
                durationUnit.getSelectedItem(), barSize.getSelectedItem(),
                whatToShow.getSelectedItem(),
                regularTradingHoursOnly.isSelected(), panel);

        resultsPanel.addTab("Historical " + newContract.symbol(), panel, true,
                true);
    }
}