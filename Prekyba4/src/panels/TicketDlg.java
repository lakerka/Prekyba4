package panels;

import handlers.OrdersHandler;
import initial.IllegalArgumentsValidator;
import initial.JTextFieldNumbersSupported;
import initial.TCombo;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import utilities.Support;
import buttons.HtmlButton;

import com.ib.client.TagValue;
import com.ib.controller.NewContract;
import com.ib.controller.NewOrder;
import com.ib.controller.NewOrderState;
import com.ib.controller.OrderType;
import com.ib.controller.Types.Action;
import com.ib.controller.Types.ComboParam;
import com.ib.controller.Types.TimeInForce;

import controllers.ConnectionController;
import controllers.MainController;

public class TicketDlg extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 3750329387563176151L;

    private boolean m_editContract;
    private  NewContract m_contract;
    private  NewOrder m_order;
    private  ContractPanel m_contractPanel;
    private  OrderPanel m_orderPanel;
    private OrdersHandler ordersHandler;
    private MainController mainController;

    public TicketDlg(NewContract contract, NewOrder order, Frame parentFrame,
            MainController mainController) {

        super(parentFrame);
        
        IllegalArgumentsValidator.checkNullPointerPassed(mainController);
        
        this.mainController = mainController;
        this.ordersHandler = mainController.getOrdersHandler();

        if (contract == null) {
            contract = new NewContract();
            m_editContract = true;
        }

        if (order == null) {
            order = new NewOrder();
            order.totalQuantity(100);
            order.lmtPrice(1);
        }

        m_contract = contract;
        m_order = order;

        m_contractPanel = new ContractPanel(m_contract);
        m_orderPanel = new OrderPanel();

        HtmlButton transmitOrder = new HtmlButton("Transmit Order") {
            /**
             * 
             */
            private static final long serialVersionUID = 8608954987786000428L;

            @Override
            public void actionPerformed() {
                onTransmitOrder();
            }
        };

        HtmlButton close = new HtmlButton("Close") {
            /**
             * 
             */
            private static final long serialVersionUID = 1449068704255780560L;

            @Override
            public void actionPerformed() {
                dispose();
            }
        };

        NewTabbedPanel tabbedPanel = new NewTabbedPanel(true);
        if (m_editContract) {
            tabbedPanel.addTab("Contract", m_contractPanel);
        }
        tabbedPanel.addTab("Order", m_orderPanel);

        JPanel buts = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buts.add(transmitOrder);
        buts.add(close);

        add(tabbedPanel);
        add(buts, BorderLayout.SOUTH);

        setLocation(200, 200);
        pack();
        Support.closeOnEsc(this);
    }

    protected void onTransmitOrder() {
        scrape();

        // close window right away for mods
        if (m_order.orderId() != 0) {
            dispose();
        }
        // TODO find out what this means
        this.ordersHandler.placeOrModifyOrder(m_contract, m_order,

        new interfaces.IOrderHandler() {

            // activated when order data received from server
            @Override
            public void orderState(NewOrderState orderState) {

                // remove current handler
                ordersHandler.removeOrderHandler(this);

                // destroy current dialaog window
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dispose();
                    }
                });

            }

            @Override
            public void handle(int errorCode, final String errorMsg) {
                m_order.orderId(0);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(TicketDlg.this, errorMsg);
                    }
                });
            }
        });
    }

    private void scrape() {
        m_order.smartComboRoutingParams().clear();

        if (m_editContract) {
            m_contractPanel.onOK();
        }
        m_orderPanel.onOK();
    }

    class OrderPanel extends VerticalPanel {

        /**
         * 
         */
        private static final long serialVersionUID = -6292501665184821336L;
        final TCombo<String> m_account = new TCombo<String>(
                mainController.getConnectionController().getAccountList().toArray(new String[0]));
        final TCombo<Action> m_action = new TCombo<Action>(Action.values());
        final JTextFieldNumbersSupported m_quantity = new JTextFieldNumbersSupported(
                "100");
        final JTextFieldNumbersSupported m_displaySize = new JTextFieldNumbersSupported();
        final TCombo<OrderType> m_orderType = new TCombo<OrderType>(
                OrderType.values());
        final JTextFieldNumbersSupported m_lmtPrice = new JTextFieldNumbersSupported(
                "200");
        final JTextFieldNumbersSupported m_auxPrice = new JTextFieldNumbersSupported();
        final TCombo<TimeInForce> m_tif = new TCombo<TimeInForce>(
                TimeInForce.values());
        final JCheckBox m_nonGuaranteed = new JCheckBox();

        OrderPanel() {
                   
            m_orderType.removeItemAt(0); // remove None

            m_account.setSelectedItem(m_order.account() != null ? m_order
                    .account() : mainController.getConnectionController().getAccountList().get(0));
            m_action.setSelectedItem(m_order.action());
            m_quantity.setText(m_order.totalQuantity());
            m_displaySize.setText(m_order.displaySize());
            m_orderType.setSelectedItem(m_order.orderType());
            m_lmtPrice.setText(m_order.lmtPrice());
            m_auxPrice.setText(m_order.auxPrice());
            m_tif.setSelectedItem(m_order.tif());
            m_nonGuaranteed.setSelected(getVal(ComboParam.NonGuaranteed)
                    .equals("1"));

            add("Account", m_account);
            add("Action", m_action);
            add("Quantity", m_quantity);
            add("Display size", m_displaySize);
            add("Order type", m_orderType);
            add("Limit price", m_lmtPrice);
            add("Aux price", m_auxPrice);
            add("Time-in-force", m_tif);
            if (m_contract.isCombo()) {
                add("Non-guaranteed", m_nonGuaranteed);
            }
        }

        private void onOK() {
            m_order.account(m_account.getText().toUpperCase());
            m_order.action(m_action.getSelectedItem());
            m_order.totalQuantity(m_quantity.getInt());
            m_order.displaySize(m_displaySize.getInt());
            m_order.orderType(m_orderType.getSelectedItem());
            m_order.lmtPrice(m_lmtPrice.getDouble());
            m_order.auxPrice(m_auxPrice.getDouble());
            m_order.tif(m_tif.getSelectedItem());
            if (m_contract.isCombo()) {
                TagValue tv = new TagValue(ComboParam.NonGuaranteed.toString(),
                        m_nonGuaranteed.isSelected() ? "1" : "0");
                m_order.smartComboRoutingParams().add(tv);
            }
        }
    }

    /**
     * This panel edits all ComboParam values except for Non-Guaranteed. That
     * one goes on main panel because it applies to all combo orders.
     */
    class ComboTicketPanel extends VerticalPanel {
        /**
         * 
         */
        private static final long serialVersionUID = 3206840805833863540L;
        final JTextFieldNumbersSupported[] m_fields = new JTextFieldNumbersSupported[ComboParam
                .values().length];

        ComboTicketPanel() {
            for (ComboParam param : ComboParam.values()) {
                if (param == ComboParam.NonGuaranteed) {
                    continue;
                }
                JTextFieldNumbersSupported field = new JTextFieldNumbersSupported();
                m_fields[param.ordinal()] = field;
                add(param.toString(), field);
                field.setText(getVal(param));
            }
        }

        void onOK() {
            for (ComboParam param : ComboParam.values()) {
                if (param == ComboParam.NonGuaranteed) {
                    continue;
                }
                JTextFieldNumbersSupported field = m_fields[param.ordinal()];
                String val = field.getText();
                if (val != null && val.length() > 0) {
                    TagValue tv = new TagValue(param.toString(), val);
                    m_order.smartComboRoutingParams().add(tv);
                }
            }
        }
    }

    class ScalePanel extends VerticalPanel {
        /**
         * 
         */
        private static final long serialVersionUID = 1836000887822712907L;

        JTextFieldNumbersSupported m_initLevelSize = new JTextFieldNumbersSupported();
        JTextFieldNumbersSupported m_subsLevelSize = new JTextFieldNumbersSupported();
        JTextFieldNumbersSupported m_priceIncrement = new JTextFieldNumbersSupported();
        JTextFieldNumbersSupported m_priceAdjustValue = new JTextFieldNumbersSupported();
        JTextFieldNumbersSupported m_priceAdjustInterval = new JTextFieldNumbersSupported();
        JTextFieldNumbersSupported m_profitOffset = new JTextFieldNumbersSupported();
        JCheckBox m_autoReset = new JCheckBox();
        JTextFieldNumbersSupported m_initPosition = new JTextFieldNumbersSupported();
        JTextFieldNumbersSupported m_initFillQty = new JTextFieldNumbersSupported();
        JCheckBox m_randomPercent = new JCheckBox();
        JTextFieldNumbersSupported m_table = new JTextFieldNumbersSupported();

        ScalePanel() {
            m_initLevelSize.setText(m_order.scaleInitLevelSize());
            m_subsLevelSize.setText(m_order.scaleSubsLevelSize());
            m_priceIncrement.setText(m_order.scalePriceIncrement());
            m_priceAdjustValue.setText(m_order.scalePriceAdjustValue());
            m_priceAdjustInterval.setText(m_order.scalePriceAdjustInterval());
            m_profitOffset.setText(m_order.scaleProfitOffset());
            m_autoReset.setSelected(m_order.scaleAutoReset());
            m_initPosition.setText(m_order.scaleInitPosition());
            m_initFillQty.setText(m_order.scaleInitFillQty());
            m_randomPercent.setSelected(m_order.scaleRandomPercent());
            m_table.setText(m_order.scaleTable());

            add("Initial comp size", m_initLevelSize);
            add("Subsequent comp size", m_subsLevelSize);
            add("Randomize size", m_randomPercent);
            add(Box.createVerticalStrut(10));
            add("Price increment", m_priceIncrement);
            add("Profit offset", m_profitOffset);
            add("Auto-reset", m_autoReset);
            add(Box.createVerticalStrut(10));
            add("Initial position", m_initPosition);
            add("Filled init comp size", m_initFillQty);
            add(Box.createVerticalStrut(10));
            add("Increase price by", m_priceAdjustValue, new JLabel("every"),
                    m_priceAdjustInterval, new JLabel("seconds"));
            add(Box.createVerticalStrut(10));
            add("Manual table", m_table);
        }

        void onOK() {
            m_order.scaleInitLevelSize(m_initLevelSize.getInt());
            m_order.scaleSubsLevelSize(m_subsLevelSize.getInt());
            m_order.scalePriceIncrement(m_priceIncrement.getDouble());
            m_order.scalePriceAdjustValue(m_priceAdjustValue.getDouble());
            m_order.scalePriceAdjustInterval(m_priceAdjustInterval.getInt());
            m_order.scaleProfitOffset(m_profitOffset.getDouble());
            m_order.scaleAutoReset(m_autoReset.isSelected());
            m_order.scaleInitPosition(m_initPosition.getInt());
            m_order.scaleInitFillQty(m_initFillQty.getInt());
            m_order.scaleRandomPercent(m_randomPercent.isSelected());
            m_order.scaleTable(m_table.getText());
        }
    }

    private String getVal(ComboParam param) {
        if (m_order.smartComboRoutingParams() != null) {
            for (TagValue tv : m_order.smartComboRoutingParams()) {
                if (tv.m_tag.equals(param.toString())) {
                    return tv.m_value;
                }
            }
        }
        return "";
    }
}
