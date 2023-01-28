package com.example.hikergram;

import java.util.ArrayList;

public class PostListSingleton {
    private static PostListSingleton instance;
    private ArrayList<Post> postList;

    private PostListSingleton() {
        postList = new ArrayList<>();
    }

    public static PostListSingleton getInstance() {
        if (instance == null) {
            instance = new PostListSingleton();
        }
        return instance;
    }

    public ArrayList<Post> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<Post> postList) {
        this.postList = postList;
    }
}