package com.example.soubhagya.finalhackathon;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by soubhagya on 2/4/17.
 */

public class User {

    private String phoneNumber;
    private String userId;
    private ConcurrentHashMap<String,Boolean> trustedContacts;
    private ConcurrentHashMap<String,Boolean> dependents;

    public User(){}

    public User(String number, String id){
        phoneNumber = number;
        userId = id;
        trustedContacts = new ConcurrentHashMap<>();
        dependents = new ConcurrentHashMap<>();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ConcurrentHashMap<String, Boolean> getTrustedContacts() {
        return trustedContacts;
    }

    public void setTrustedContacts(ConcurrentHashMap<String, Boolean> trustedContacts) {
        this.trustedContacts = trustedContacts;
    }

    public ConcurrentHashMap<String, Boolean> getDependents() {
        return dependents;
    }

    public void setDependents(ConcurrentHashMap<String, Boolean> dependents) {
        this.dependents = dependents;
    }
}
