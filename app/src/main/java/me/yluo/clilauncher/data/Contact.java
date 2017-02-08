package me.yluo.clilauncher.data;

import java.util.List;

/**
 * Created by yang on 16-8-11.
 */
public class Contact {
    public String name;
    public int callTimes;
    public List<String> phones;

    public Contact(String name, String callTimes, List<String> phones) {
        this.name = name;
        this.callTimes = Integer.parseInt(callTimes);
        this.phones = phones;
    }

    public void addPhone(String phone) {
        if (phones != null) {
            phones.add(phone);
        }
    }

    @Override
    public String toString() {
        return "name:" + name + " phone:" + phones.toString() + " calltimes:" + callTimes;
    }
}
