package com.example.ladvi.chernobylproject;

import java.util.ArrayList;
import java.util.List;


public class LandmarkController
{
    private List<Landmark> landmarks = new ArrayList<Landmark>();

    public ArrayList<Landmark> getLandmarks() {
        ArrayList<Landmark> landmarks = new ArrayList<Landmark>();
        landmarks.add(new Landmark("Pripyat main square", "The central square of city of Pripyat. Hotel Polesia is the dominant landmark in this area.", 51.406145, 30.057325, 0, R.drawable.logo, new int[] {
                R.drawable.testimage, R.drawable.logo1, R.drawable.offline, R.drawable.logo, R.drawable.online
        }));
        landmarks.add(new Landmark("Ferris wheel", "Not very contaminated, readings 0,5 microsievert/h", 51.408340, 30.055810, 1, R.drawable.offline, new int[] {}));
        landmarks.add(new Landmark("Yanov train station", "Contaminated place,dont stay long. Readigs up to 400 microsiev/h", 51.394344, 30.057784, 2, R.drawable.online, new int[] {}));
        landmarks.add(new Landmark("Red Forest", "Severely contaminated, avoid. Readings over 500microsievert/h", 51.386696, 30.088431, 3, R.drawable.green, new int[] {}));
        return landmarks;
    }
}
