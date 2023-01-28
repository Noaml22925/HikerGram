package com.example.hikergram;

import java.io.Serializable;
import java.util.*;


public class Post implements Serializable {
    // Fields
    private User user;
    private String nameOfTheHike;
    private String description;
    private String imageUrl;
    private int difficulty;
    private int likes;
    private ArrayList<Comment> comments;
    private String uuid;


    // Constructor
    public Post(){}

    public Post(User user, String nameOfTheHike, String description, String imageUrl, int difficulty, String uuid) {
        this.user = user;
        this.nameOfTheHike = nameOfTheHike;
        this.description = description;
        this.imageUrl = imageUrl;
        this.difficulty = difficulty;
        this.likes = 0;
        this.comments = new ArrayList<Comment>();
        this.uuid = uuid;
    }


    // Getters and setters


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNameOfTheHike() {
        return nameOfTheHike;
    }

    public void setNameOfTheHike(String nameOfTheHike) {
        this.nameOfTheHike = nameOfTheHike;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getUuid() {
        return uuid;
    }


    // Other methods
    public void like() {
        this.likes++;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    @Override
    public String toString() {
        return "Post{" +
                "user=" + user +
                ", nameOfTheHike='" + nameOfTheHike + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", difficulty=" + difficulty +
                ", likes=" + likes +
                ", comments=" + comments +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return getDifficulty() == post.getDifficulty() && getLikes() == post.getLikes() && Objects.equals(getUser(), post.getUser()) && Objects.equals(getNameOfTheHike(), post.getNameOfTheHike()) && Objects.equals(getDescription(), post.getDescription()) && Objects.equals(getImageUrl(), post.getImageUrl()) && Objects.equals(getComments(), post.getComments()) && Objects.equals(getUuid(), post.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getNameOfTheHike(), getDescription(), getImageUrl(), getDifficulty(), getLikes(), getComments(), getUuid());
    }
}