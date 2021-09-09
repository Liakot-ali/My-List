package com.liakot.mywish;

public class UserProfile {
    private  String firstName, lastName, email, password, phoneNumber, profilePicRef, work;

    public UserProfile() {

    }

    public UserProfile(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserProfile(String firstName, String lastName, String email, String password, String phoneNumber, String profilePicRef, String work) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profilePicRef = profilePicRef;
        this.work = work;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicRef() {
        return profilePicRef;
    }

    public void setProfilePicRef(String profilePicRef) {
        this.profilePicRef = profilePicRef;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }


}
