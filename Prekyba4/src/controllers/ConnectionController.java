package controllers;

import initial.EClientSocketObserved;
import interfaces.IConnectionHandler;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.AlreadyConnectedException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.ib.client.AnyWrapper;
import com.ib.client.Builder;
import com.ib.client.ComboLeg;
import com.ib.client.Contract;
import com.ib.client.EClientErrors;
import com.ib.client.EClientSocket;
import com.ib.client.TagValue;
import com.ib.controller.Formats;
import com.ib.controller.NewComboLeg;
import com.ib.controller.NewContract;
import com.ib.controller.NewOrder;
import com.ib.controller.OrderType;
import com.ib.controller.ApiController.IBulletinHandler;
import com.ib.controller.ApiController.ITimeHandler;
import com.ib.controller.Types.AlgoStrategy;
import com.ib.controller.Types.HedgeType;
import com.ib.controller.Types.NewsType;
import com.ib.controller.Types.SecType;

public class ConnectionController extends EClientSocketObserved implements
        IConnectionHandler {

    private ArrayList<String> accountList = new ArrayList<String>();

    public ConnectionController(AnyWrapper anyWrapper,
            int connectivityLookupDelayInMiliseconds) {

        super(anyWrapper, connectivityLookupDelayInMiliseconds);
    }

    public List<String> getAccountList() {

        return this.accountList;
    }
    
    public synchronized void placeOrder(NewContract contract, NewOrder order) {
        // not connected?
        if( !isConnected() ) {
            notConnected();
            return;
        }

        // ApiController requires TWS 932 or higher; this limitation could be removed if needed
        if (serverVersion() < 66) {
            error( EClientErrors.NO_VALID_ID, EClientErrors.UPDATE_TWS, "ApiController requires TWS build 932 or higher to place orders.");
            return;
        }

        Builder b = new Builder();

        int VERSION = 41;

        // send place order msg
        try {
            b.send( PLACE_ORDER);
            b.send( VERSION);
            b.send( order.orderId() );
            b.send( contract.conid() );
            b.send( contract.symbol());
            b.send( contract.secType() );
            b.send( contract.expiry());
            b.send( contract.strike());
            b.send( contract.right().getApiString() );
            b.send( contract.multiplier() );
            b.send( contract.exchange() );
            b.send( contract.primaryExch() );
            b.send( contract.currency() );
            b.send( contract.localSymbol() );
            if (m_serverVersion >= MIN_SERVER_VER_TRADING_CLASS) {
                b.send(contract.tradingClass() );
            }
            b.send( contract.secIdType() );
            b.send( contract.secId() );
            b.send( order.action() );
            b.send( order.totalQuantity() );
            b.send( order.orderType() );
            b.send( order.lmtPrice() );
            b.send( order.auxPrice() );
            b.send( order.tif() );
            b.send( order.ocaGroup() );
            b.send( order.account() );
            b.send( ""); // open/close
            b.send( ""); // origin
            b.send( order.orderRef() );
            b.send( order.transmit() );
            b.send( order.parentId() );
            b.send( order.blockOrder() );
            b.send( order.sweepToFill() );
            b.send( order.displaySize() );
            b.send( order.triggerMethod() );
            b.send( order.outsideRth() );
            b.send( order.hidden() );

            // send combo legs for BAG orders
            if(contract.secType() == SecType.BAG) {
                b.send( contract.comboLegs().size());
                
                for (NewComboLeg leg : contract.comboLegs() ) {
                    
                    b.send( leg.conid() );
                    b.send( leg.ratio() );
                    b.send( leg.action().getApiString() );
                    b.send( leg.exchange() );
                    b.send( leg.openClose().getApiString() );
                    b.send( leg.shortSaleSlot() );
                    b.send( leg.designatedLocation() );
                    b.send( leg.exemptCode() );
                }

                b.send( order.orderComboLegs().size());
                for (Double orderComboLeg : order.orderComboLegs() ) {
                    b.send( orderComboLeg);
                }

                b.send( order.smartComboRoutingParams().size() );
                for (TagValue tagValue : order.smartComboRoutingParams() ) {
                    b.send( tagValue.m_tag);
                    b.send( tagValue.m_value);
                }
            }

            b.send( ""); // obsolete field
            b.send( order.discretionaryAmt() );
            b.send( order.goodAfterTime() );
            b.send( order.goodTillDate() );
            b.send( order.faGroup());
            b.send( order.faMethod() );
            b.send( order.faPercentage() );
            b.send( order.faProfile());
            b.send( 0); // short sale slot
            b.send( ""); // designatedLocation
            b.send( ""); // exemptCode
            b.send( order.ocaType() );
            b.send( order.rule80A() );
            b.send( ""); // settlingFirm
            b.send( order.allOrNone() );
            b.send( order.minQty() );
            b.send( order.percentOffset() );
            b.send( order.eTradeOnly() );
            b.send( order.firmQuoteOnly() );
            b.send( order.nbboPriceCap() );
            b.send( order.auctionStrategy() );
            b.send( order.startingPrice() );
            b.send( order.stockRefPrice() );
            b.send( order.delta() );
            b.send( order.stockRangeLower() );
            b.send( order.stockRangeUpper() );
            b.send( order.overridePercentageConstraints() );
            b.send( order.volatility() );
            b.send( order.volatilityType() );
            b.send( order.deltaNeutralOrderType() );
            b.send( order.deltaNeutralAuxPrice() );

            if (order.deltaNeutralOrderType() != OrderType.None) {
                b.send( order.deltaNeutralConId() );
                b.send( ""); //deltaNeutralSettlingFirm
                b.send( ""); //deltaNeutralClearingAccount
                b.send( ""); //deltaNeutralClearingIntent
                b.send( ""); //deltaNeutralOpenClose
                b.send( ""); //deltaNeutralShortSale
                b.send( ""); //deltaNeutralShortSaleSlot
                b.send( ""); //deltaNeutralDesignatedLocation
            }
            
            b.send( order.continuousUpdate() );
            b.send( order.referencePriceType() );
            b.send( order.trailStopPrice() );
            b.send( order.trailingPercent() );
            b.send( order.scaleInitLevelSize() );
            b.send( order.scaleSubsLevelSize() );
            b.send( order.scalePriceIncrement() );

            if (order.scalePriceIncrement() != 0 && order.scalePriceIncrement() != Double.MAX_VALUE) {
                b.send( order.scalePriceAdjustValue() );
                b.send( order.scalePriceAdjustInterval() );
                b.send( order.scaleProfitOffset() );
                b.send( order.scaleAutoReset() );
                b.send( order.scaleInitPosition() );
                b.send( order.scaleInitFillQty() );
                b.send( order.scaleRandomPercent() );
            }

            if (m_serverVersion >= MIN_SERVER_VER_SCALE_TABLE) {
                b.send( order.scaleTable() );
                b.send( ""); // active start time
                b.send( ""); // active stop time
            }

            b.send( order.hedgeType() );
            if (order.hedgeType() != HedgeType.None) {
                b.send( order.hedgeParam() );
            }

            b.send( order.optOutSmartRouting() );
            b.send( "");//clearingAccount
            b.send( "");//clearingIntent
            b.send( order.notHeld() );

            b.send( contract.underComp() != null);
            if (contract.underComp() != null) {
                b.send( contract.underComp().conid() );
                b.send( contract.underComp().delta() );
                b.send( contract.underComp().price() );
            }

            b.send( order.algoStrategy() );
            if( order.algoStrategy() != AlgoStrategy.None) {
                b.send( order.algoParams().size() );
                for( TagValue tagValue : order.algoParams() ) {
                    b.send( tagValue.m_tag);
                    b.send( tagValue.m_value);
                }
            }

            b.send( order.whatIf() );

            m_dos.write( b.getBytes() );
        }
        catch( Exception e) {
            e.printStackTrace();
            error( order.orderId(), 512, "Order sending error - " + e);
            close();
        }
    }

    @Override
    public void connected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void accountList(ArrayList<String> list) {

        accountList.clear();
        accountList.addAll(list);

    }

    @Override
    public void error(Exception e) {
        // TODO Auto-generated method

    }

    @Override
    public void message(int id, int errorCode, String errorMsg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void show(String string) {
        // TODO Auto-generated method stub

    }

}
