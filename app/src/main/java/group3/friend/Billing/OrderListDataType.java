package group3.friend.Billing;

import java.util.Date;

public class OrderListDataType {
    private int memberid, orderid;
    private Date orderdate;

    public OrderListDataType(int memberId) {
        this.memberid = memberId;
    }

    public OrderListDataType(int orderId, int memberId, Date time) {
        this(memberId);
        this.orderid = orderid;
        this.orderdate = orderdate;
    }

    public int getMemberid() {
        return memberid;
    }

    public int getOrderid() {
        return orderid;
    }

    public Date getOrderdate() {
        return orderdate;
    }


}

