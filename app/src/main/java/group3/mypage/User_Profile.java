package group3.mypage;


import java.io.Serializable;

public class User_Profile implements Serializable {

    private int memberId;
    private String email;
    private String password;
    private String userName;
    private String selfIntroduction;
    private int vipStatus;

    public User_Profile(int memberId, String email, String password, String userName, String selfIntroduction, int vipStatus) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.selfIntroduction = selfIntroduction;
        this.vipStatus = vipStatus;
    }

    public User_Profile(int memberId,String userName, String selfIntroduction) {
        super();
        this.memberId = memberId;
        this.userName = userName;
        this.selfIntroduction = selfIntroduction;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    public int getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(int vipStatus) {
        this.vipStatus = vipStatus;
    }
}