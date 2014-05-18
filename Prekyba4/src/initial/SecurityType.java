package initial;

public enum SecurityType {
    None, STK, OPT, FUT, CASH, BOND, CFD, FOP, WAR, IOPT, FWD, BAG, IND, BILL, FUND, FIXED, SLB, NEWS, CMDTY, BSK, ICU, ICS;

    public String getString() {
        return this == None ? "" : super.toString();
    }
}
