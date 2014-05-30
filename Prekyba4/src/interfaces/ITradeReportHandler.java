package interfaces;

import com.ib.client.CommissionReport;
import com.ib.client.Execution;
import com.ib.controller.NewContract;

public interface ITradeReportHandler {
    void tradeReport(String tradeKey, NewContract contract, Execution execution);
    void tradeReportEnd();
    void commissionReport(String tradeKey, CommissionReport commissionReport);
}
