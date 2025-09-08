// NotificationDAO.java
package com.example.attendance.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationDAO {
    private static final Map<String, List<String>> notifications = new HashMap<>();

    public void add(String user, String message) {
        notifications.computeIfAbsent(user, k -> new ArrayList<>()).add(message);
    }

    public List<String> fetchAndClear(String user) {
        List<String> list = notifications.getOrDefault(user, new ArrayList<>());
        notifications.remove(user);
        return list;
    }
}
