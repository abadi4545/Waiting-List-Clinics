package com.abadi.waitinglistclinics.Model;

public class UserModel {
    private String id;
    private String username;
    private String email;
    private String imageURL;
    private String type;
    private String terdaftar;

    public UserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTerdaftar() {
        return terdaftar;
    }

    public void setTerdaftar(String terdaftar) {
        this.terdaftar = terdaftar;
    }
}
