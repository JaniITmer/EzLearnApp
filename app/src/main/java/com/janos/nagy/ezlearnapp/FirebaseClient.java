package com.janos.nagy.ezlearnapp;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseClient {

    private static FirebaseFirestore db;

    public static FirebaseFirestore getFirestore(){

        if(db==null){
            db=FirebaseFirestore.getInstance();
        }
        return db;

    }
}
