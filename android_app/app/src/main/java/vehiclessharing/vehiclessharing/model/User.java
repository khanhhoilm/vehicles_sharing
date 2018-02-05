package vehiclessharing.vehiclessharing.model;

/**
 * Created by Hihihehe on 9/27/2017.
 */

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("avatar_link")
    @Expose
    private String avatarLink;
    @SerializedName("gender")
    @Expose
    private Integer gender;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("birthday")
    @Expose
    private String birthday;

    @SerializedName("avg_hiker_vote")
    @Expose
    private int avgHikerVote;

    @SerializedName("avg_driver_vote")
    @Expose
    private int avgDriverVote;

    @SerializedName("is_favorite")
    @Expose
    private int isFavorite;

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int getAvgHikerVote() {
        return avgHikerVote;
    }

    public void setAvgHikerVote(int avgHikerVote) {
        this.avgHikerVote = avgHikerVote;
    }

    public int getAvgDriverVote() {
        return avgDriverVote;
    }

    public void setAvgDriverVote(int avgDriverVote) {
        this.avgDriverVote = avgDriverVote;
    }

    private Bitmap picture;

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}


