package com.avelon.turf.data;

import com.avelon.turf.Logger;
import java.util.HashMap;
import java.util.List;

public class Users extends HashMap<String, User> {
    private Logger logger = new Logger(Users.class);

    public synchronized void setUsers(List<User> users) {
        this.setAllCurrentToHidden();

        for(User user : users) {
            user.hidden = false;
            this.put(user.name, user);
        }
    }

    private void setAllCurrentToHidden() {
        for(User user : this.values()) {
            user.hidden = true;
        }
    }

    public String toString() {
        String str = "";
        for(User user : this.values()) {
            str += "{" + user + "}";
        }
        return str;
    }
}
