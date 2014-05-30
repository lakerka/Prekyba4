package panels;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import model.TopModel;
import panels.NewTabbedPanel.NewTabPanel;
import handlers.TopDataResultRowsHandler;
import interfaces.*;
import initial.*;

import com.ib.client.Contract;
import com.ib.controller.NewContract;
import com.ib.controller.Types.MktDataType;

public class TopResultsTablePanel extends NewTabPanel {

    final TopModel topModel;
    final JTable jTableTab;
    final TCombo<MktDataType> typeCombo = new TCombo<MktDataType>(
            MktDataType.values());
    
    final TopRequestPanel topRequestPanel;

    public TopResultsTablePanel(
            TopDataResultRowsHandler topDataResultRowsHandler, TopRequestPanel topRequestPanel) {

        IllegalArgumentsValidator.checkNullPointerPassed(topDataResultRowsHandler, topRequestPanel);
        
        topModel = new TopModel(topDataResultRowsHandler);
        jTableTab = new TopTable(topModel);
        this.topRequestPanel = topRequestPanel;
        
        typeCombo.removeItemAt(0);

        JScrollPane scroll = new JScrollPane(jTableTab);


        setLayout(new BorderLayout());
        add(scroll);
    }
    
    /** Add row to topModel. */
    public void addRowToTopModel(NewContract newContract) {
        
        this.topModel.addRow(newContract);
    }

    /** Called when the tab is first visited. */
    @Override
    public void activated() {
    }

    /** Called when the tab is closed by clicking the X. */
    @Override
    public void closed() {
        topModel.desubscribe();
        topRequestPanel.destroyTopResultsTablePanel();
    }


    class TopTable extends JTable {
        public TopTable(TopModel model) {
            super(model);
        }

        @Override
        public TableCellRenderer getCellRenderer(int rowIn, int column) {
            TableCellRenderer rend = super.getCellRenderer(rowIn, column);
            topModel.color(rend, rowIn, getForeground());
            return rend;
        }
    }
}
