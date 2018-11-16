package group3.friend.Chat;

import java.util.Set;

public class StateMessage {
    private String userName;
    private String type;
    // the user changing the state
    private String user;
    // total users
    private Set<String> users;

    public StateMessage(String type, String user, Set<String> users, String userName) {
        super();
        this.type = type;
        this.user = user;
        this.users = users;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

}
