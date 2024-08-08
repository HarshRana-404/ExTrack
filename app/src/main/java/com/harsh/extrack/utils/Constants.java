package com.harsh.extrack.utils;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Constants {
    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore fbStore;
    public static FirebaseAuth fbAuth;
    public static String uID;
    public static void init(){
        fbStore = FirebaseFirestore.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        uID = fbAuth.getUid();
    }
}
