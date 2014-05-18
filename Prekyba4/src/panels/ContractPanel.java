package panels;

import initial.JTextFieldNumbersSupported;
import initial.TCombo;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.ib.controller.NewContract;
import com.ib.controller.Types.Right;
import com.ib.controller.Types.SecType;

public class ContractPanel extends JPanel{

    protected JTextFieldNumbersSupported symbol = new JTextFieldNumbersSupported();
    protected TCombo<SecType> securityType = new TCombo<SecType>( SecType.values() );
    protected JTextFieldNumbersSupported expiry = new JTextFieldNumbersSupported();
    protected JTextFieldNumbersSupported m_strike = new JTextFieldNumbersSupported();
    protected TCombo<Right> m_right = new TCombo<Right>( Right.values() );
    protected JTextFieldNumbersSupported m_multiplier = new JTextFieldNumbersSupported();
    protected JTextFieldNumbersSupported m_exchange = new JTextFieldNumbersSupported();
    protected JTextFieldNumbersSupported m_currency = new JTextFieldNumbersSupported();
    protected JTextFieldNumbersSupported m_localSymbol = new JTextFieldNumbersSupported();
    protected JTextFieldNumbersSupported m_tradingClass = new JTextFieldNumbersSupported();

    private NewContract newContract;

    ContractPanel(NewContract c) {
        newContract = c;

        if (c.secType() == SecType.None) {
            symbol.setText( "IBM");
            securityType.setSelectedItem( SecType.STK);
            m_exchange.setText( "SMART"); 
            m_currency.setText( "USD");
        }
        else {
            symbol.setText( newContract.symbol());
            securityType.setSelectedItem( newContract.secType() );
            expiry.setText( newContract.expiry());
            m_strike.setText( "" + newContract.strike() );
            m_right.setSelectedItem( newContract.right() ); 
            m_multiplier.setText( newContract.multiplier() );
            m_exchange.setText( newContract.exchange()); 
            m_currency.setText( newContract.currency());
            m_localSymbol.setText( newContract.localSymbol());
            m_tradingClass.setText( newContract.tradingClass() );
        }
        
        VerticalPanel p = new VerticalPanel();
        p.add( "Symbol", symbol);
        p.add( "Sec type", securityType);
        p.add( "Expiry", expiry);
        p.add( "Strike", m_strike);
        p.add( "Put/call", m_right);
        p.add( "Multiplier", m_multiplier);
        p.add( "Exchange", m_exchange);
        p.add( "Currency", m_currency);
        p.add( "Local symbol", m_localSymbol);
        p.add( "Trading class", m_tradingClass);
        
        setLayout( new BorderLayout() );
        add( p);
    }
    
    @Override public Dimension getMaximumSize() {
        return super.getPreferredSize();
    }
    
    public void onOK() {
        if (newContract.isCombo() ) {
            return;
        }
        
        newContract.symbol( symbol.getText().toUpperCase() ); 
        newContract.secType( securityType.getSelectedItem() ); 
        newContract.expiry( expiry.getText() ); 
        newContract.strike( m_strike.getDouble() ); 
        newContract.right( m_right.getSelectedItem() ); 
        newContract.multiplier( m_multiplier.getText() ); 
        newContract.exchange( m_exchange.getText().toUpperCase() ); 
        newContract.currency( m_currency.getText().toUpperCase() ); 
        newContract.localSymbol( m_localSymbol.getText().toUpperCase() );
        newContract.tradingClass( m_tradingClass.getText().toUpperCase() );
    }

}
