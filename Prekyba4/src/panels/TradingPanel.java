/* Copyright (C) 2013 Interactive Brokers LLC. All rights reserved.  This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package panels;

import initial.IllegalArgumentsValidator;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BoxLayout;

import controllers.MainController;
import panels.NewTabbedPanel.NewTabPanel;

/**
 * main traiding panel tab where is live orders and trades log panels
 */
public class TradingPanel extends NewTabPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -1778579951134274105L;

    private final OrdersPanel m_ordersPanel;

    private final TradesPanel m_tradesPanel;

    public TradingPanel(Frame parentFrame, MainController mainController) {

        IllegalArgumentsValidator.checkNullPointerPassed(parentFrame,
                mainController);

        m_ordersPanel = new OrdersPanel(parentFrame, mainController);

        m_tradesPanel = new TradesPanel(mainController);

        m_ordersPanel.setPreferredSize(new Dimension(1, 10000));
        m_tradesPanel.setPreferredSize(new Dimension(1, 10000));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(m_ordersPanel);
        add(m_tradesPanel);
    }

    /** Called when the tab is first visited. */
    @Override
    public void activated() {
        m_ordersPanel.activated();
        m_tradesPanel.activated();
    }

    /** Called when the tab is closed by clicking the X. */
    @Override
    public void closed() {
    }
}
