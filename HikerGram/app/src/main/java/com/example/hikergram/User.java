package com.example.hikergram;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class User {
   private String email;
   private String username;
   private String password;
   private int age;
   private String profilePic;

   public User(){}

    public User(String email, String username, String password, int age, String profilePic) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.age = age;
        this.profilePic = profilePic;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }



    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", profilePic='" + profilePic + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getAge() == user.getAge() && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getProfilePic(), user.getProfilePic());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getUsername(), getPassword(), getAge(), getProfilePic());
    }
}



