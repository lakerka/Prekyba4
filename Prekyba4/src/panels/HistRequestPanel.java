package panels;

import initial.TCombo;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Types.WhatToShow;

public class HistRequestPanel extends JPanel {
    
//    final ContractPanel m_contractPanel = new ContractPanel(m_contract);
    final JTextField m_end = new JTextField();
    final JTextField m_duration = new JTextField();
    final TCombo<DurationUnit> m_durationUnit = new TCombo<DurationUnit>( DurationUnit.values() );
    final TCombo<BarSize> m_barSize = new TCombo<BarSize>( BarSize.values() );
    final TCombo<WhatToShow> m_whatToShow = new TCombo<WhatToShow>( WhatToShow.values() );
    final JCheckBox m_rthOnly = new JCheckBox();
    
    public HistRequestPanel() {        
        m_end.setText( "20130101 12:00:00");
        m_duration.setText( "1");
        m_durationUnit.setSelectedItem( DurationUnit.WEEK);
        m_barSize.setSelectedItem( BarSize._1_hour);
        
//        HtmlButton button = new HtmlButton( "Request historical data") {
//            @Override protected void actionPerformed() {
//                onHistorical();
//            }
//        };
//        
//        VerticalPanel paramPanel = new VerticalPanel();
//        paramPanel.add( "End", m_end);
//        paramPanel.add( "Duration", m_duration);
//        paramPanel.add( "Duration unit", m_durationUnit);
//        paramPanel.add( "Bar size", m_barSize);
//        paramPanel.add( "What to show", m_whatToShow);
//        paramPanel.add( "RTH only", m_rthOnly);
//        
//        VerticalPanel butPanel = new VerticalPanel();
//        butPanel.add( button);
//        
//        JPanel rightPanel = new StackPanel();
//        rightPanel.add( paramPanel);
//        rightPanel.add( Box.createVerticalStrut( 20));
//        rightPanel.add( butPanel);
//        
//        setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
//        add( m_contractPanel);
//        add( Box.createHorizontalStrut(20) );
//        add( rightPanel);
    }

    protected void onHistorical() {
//        m_contractPanel.onOK();
//        BarResultsPanel panel = new BarResultsPanel( true);
//        ApiDemo.INSTANCE.controller().reqHistoricalData(m_contract, m_end.getText(), m_duration.getInt(), m_durationUnit.getSelectedItem(), m_barSize.getSelectedItem(), m_whatToShow.getSelectedItem(), m_rthOnly.isSelected(), panel);
//        m_resultsPanel.addTab( "Historical " + m_contract.symbol(), panel, true, true);
    }
}