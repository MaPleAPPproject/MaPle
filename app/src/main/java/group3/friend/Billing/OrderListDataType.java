package group3.friend.Billing;

import java.util.Date;

public class OrderListDataType {
    private int memberid, orderid;
    private Date orderdate;

    public OrderListDataType(int orderId){
        this.memberid = memberid;
    }
    public OrderListDataType(int orderId, int memberId, Date time) {
        this.memberid = memberid;
        this.orderid = orderid;
        this.orderdate = orderdate;
    }
    public int getMemberid() {
        return memberid;
    }
    public void setMemberid(int memberid) {
        this.memberid = memberid;
    }
    public int getOrderid() {
        return orderid;
    }
    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }
    public Date getOrderdate() {
        return orderdate;
    }
    public void setOrderdate(Date orderdate) {
        this.orderdate = orderdate;
    }
}
