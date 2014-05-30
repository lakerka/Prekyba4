/* Copyright (C) 2013 Interactive Brokers LLC. All rights reserved.  This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package panels;

import handlers.LiveOrdersHandler;
import handlers.OrdersHandler;
import initial.IllegalArgumentsValidator;
import interfaces.ILiveOrderHandler;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import buttons.HtmlButton;

import com.ib.controller.NewContract;
import com.ib.controller.NewOrder;
import com.ib.controller.NewOrderState;
import com.ib.controller.OrderStatus;
import com.ib.controller.OrderType;

import controllers.ConnectionController;
import controllers.MainController;

public class OrdersPanel extends JPanel {

    private OrdersModel m_model = new OrdersModel();
    private JTable m_table = new JTable(m_model);

    private Frame pareFrame;
    private ConnectionController connectionController;
    private MainController mainController;
    // private OrdersHandler ordersHandler;
    private LiveOrdersHandler liveOrdersHandler;

    OrdersPanel(Frame parentFrame, MainController mainController) {

        IllegalArgumentsValidator.checkNullPointerPassed(parentFrame,
                mainController);

        this.pareFrame = parentFrame;
        this.mainController = mainController;
        this.connectionController = mainController.getConnectionController();
        // this.ordersHandler = mainController.getOrdersHandler();
        this.liveOrdersHandler = mainController.getLiveOrdersHandler();

        JScrollPane scroll = new JScrollPane(m_table);
        scroll.setBorder(new TitledBorder("Live Orders"));

        m_table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onDoubleClick();
                }
            }
        });

        HtmlButton ticket = new HtmlButton("Place New Order") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed() {
                onPlaceOrder();
            }
        };

        HtmlButton modify = new HtmlButton("Modify Selected Order") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed() {
                onDoubleClick();
            }
        };

        HtmlButton cancel = new HtmlButton("Cancel Selected Order") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed() {
                onCancel();
            }
        };

        HtmlButton cancelAll = new HtmlButton("Cancel All Orders") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed() {
                onCancelAll();
            }
        };

        HtmlButton refresh = new HtmlButton("Refresh") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed() {
                onRefresh();
            }
        };

        JPanel buts = new VerticalPanel();
        buts.add(ticket);
        buts.add(modify);
        buts.add(cancel);
        buts.add(cancelAll);
        buts.add(refresh);

        setLayout(new BorderLayout());
        add(buts, BorderLayout.EAST);
        add(scroll);
    }

    protected void onDoubleClick() {
        
        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }
        
        OrderRow order = getSelectedOrder();
        if (order != null) {
            TicketDlg dlg = new TicketDlg(order.m_contract, order.m_order,
                    this.pareFrame, this.mainController);
            dlg.setVisible(true);
        }
    }

    protected void onCancel() {
        OrderRow order = getSelectedOrder();
        if (order != null) {

            liveOrdersHandler.cancelOrder(order.m_order.orderId());
        }
    }

    protected void onCancelAll() {

        liveOrdersHandler.cancelAllOrders();
    }

    private void onPlaceOrder() {

        if (!mainController.isConnected()) {
            mainController.displayNotConnected();
            return;
        }

        TicketDlg dlg = new TicketDlg(null, null, pareFrame, mainController);

        dlg.setVisible(true);
    }

    protected void onAttachOrder() {
        OrderRow row = getSelectedOrder();
        if (row != null) {
            NewOrder parent = row.m_order;

            NewOrder child = new NewOrder();
            child.parentId(parent.orderId());
            child.action(parent.action());
            child.totalQuantity(parent.totalQuantity());
            child.orderType(OrderType.TRAIL);
            child.auxPrice(1);

            TicketDlg dlg = new TicketDlg(row.m_contract.clone(), child,
                    pareFrame, mainController);
            dlg.setVisible(true);
        }
    }

    protected void onRefresh() {
        m_model.clear();
        m_model.fireTableDataChanged();

        liveOrdersHandler.reqLiveOrders(m_model);
    }

    private OrderRow getSelectedOrder() {
        int i = m_table.getSelectedRow();
        return i != -1 ? m_model.get(i) : null;
    }

    public void activated() {
        onRefresh();
    }

    static class OrdersModel extends AbstractTableModel implements
            ILiveOrderHandler {

        /**
         * 
         */
        private static final long serialVersionUID = -6691850448114482924L;

        private HashMap<Long, OrderRow> m_map = new HashMap<Long, OrderRow>();
        private ArrayList<OrderRow> m_orders = new ArrayList<OrderRow>();

        @Override
        public int getRowCount() {
            return m_orders.size();
        }

        public void clear() {
            m_orders.clear();
            m_map.clear();
        }

        public OrderRow get(int i) {
            return m_orders.get(i);
        }

        @Override
        public void openOrder(NewContract contract, NewOrder order,
                NewOrderState orderState) {
            OrderRow full = m_map.get(order.permId());

            if (full != null) {
                full.m_order = order;
                full.m_state = orderState;
                fireTableDataChanged();
            } else if (shouldAdd(contract, order, orderState)) {
                full = new OrderRow(contract, order, orderState);
                add(full);
                m_map.put(order.permId(), full);
                fireTableDataChanged();
            }
        }

        protected boolean shouldAdd(NewContract contract, NewOrder order,
                NewOrderState orderState) {
            return true;
        }

        protected void add(OrderRow full) {
            m_orders.add(full);
        }

        @Override
        public void openOrderEnd() {
        }

        @Override
        public void orderStatus(int orderId, OrderStatus status, int filled,
                int remaining, double avgFillPrice, long permId, int parentId,
                double lastFillPrice, int clientId, String whyHeld) {

            OrderRow full = m_map.get(permId);
            if (full != null) {
                full.m_state.status(status);
            }
            fireTableDataChanged();
        }

        @Override
        public int getColumnCount() {
            return 8;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
            case 0:
                return "Perm ID";
            case 1:
                return "Client ID";
            case 2:
                return "Order ID";
            case 3:
                return "Account";
            case 4:
                return "Action";
            case 5:
                return "Quantity";
            case 6:
                return "Contract";
            case 7:
                return "Status";
            default:
                return null;
            }
        }

        @Override
        public Object getValueAt(int row, int col) {
            OrderRow fullOrder = m_orders.get(row);
            NewOrder order = fullOrder.m_order;
            switch (col) {
            case 0:
                return order.permId();
            case 1:
                return order.clientId();
            case 2:
                return order.orderId();
            case 3:
                return order.account();
            case 4:
                return order.action();
            case 5:
                return order.totalQuantity();
            case 6:
                return fullOrder.m_contract.description();
            case 7:
                return fullOrder.m_state.status();
            default:
                return null;
            }
        }

        @Override
        public void handle(int orderId, int errorCode, String errorMsg) {
        }
    }

    static class OrderRow {
        NewContract m_contract;
        NewOrder m_order;
        NewOrderState m_state;

        OrderRow(NewContract contract, NewOrder order, NewOrderState state) {
            m_contract = contract;
            m_order = order;
            m_state = state;
        }
    }

    static class Key {
        int m_clientId;
        int m_orderId;

        Key(int clientId, int orderId) {
            m_clientId = clientId;
            m_orderId = orderId;
        }
    }
}
