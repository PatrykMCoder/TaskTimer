package com.myapp.forest.helper;

import java.util.ArrayList;

public class ProfileUser {
    private static ArrayList<String> dataProfile = new ArrayList<>();

    public static ArrayList<String> getDataProfile() {
        return dataProfile;
    }

    public static void setDataProfile(ArrayList<String> dataProfile) {
        ProfileUser.dataProfile = dataProfile;
    }
}
