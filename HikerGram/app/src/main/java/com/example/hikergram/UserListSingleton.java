package com.example.hikergram;

import java.util.ArrayList;

public class UserListSingleton {
    private static UserListSingleton instance;
    private ArrayList<User> userList;

    private UserListSingleton() {
        userList = new ArrayList<>();
    }

    public static UserListSingleton getInstance() {
        if (instance == null) {
            instance = new UserListSingleton();
        }
        return instance;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }
}