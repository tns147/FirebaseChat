package com.example.dell.firebasetesting.ModelClasses;

public class User {
    private String username;
    private String email;
    private String userId;
    private String image;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String userId,String username, String email) {
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    public User(String image,String userId,String username,String email)
    {
        this.username = username;
        this.email = email;
        this.userId = userId;
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId)
    {this.userId = userId;}

    public String getEmail() {
        return email;
    }
    public  void setEmail(String email)
    {this.email = email;}

    public String getUsername() {
        return username;
    }
    public void setUsername(String username)
    {
        this.username=username;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image)
    {
        this.image = image;
    }
}
