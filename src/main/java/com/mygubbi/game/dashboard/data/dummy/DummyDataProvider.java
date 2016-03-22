package com.mygubbi.game.dashboard.data.dummy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import java.util.ArrayList;

import java.util.Collection;
import java.util.Collections;

import java.util.Date;

import java.util.List;

import java.util.Random;


import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mygubbi.game.dashboard.domain.DashboardNotification;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.data.DataProvider;

/**
 * A dummy implementation for the backend API.
 */
public class DummyDataProvider implements DataProvider {

    // TODO: Get API key from http://developer.rottentomatoes.com
    private static final String ROTTEN_TOMATOES_API_KEY = null;

    /* List of countries and cities for them */
    private static Multimap<String, String> countryToCities;
    private static Date lastDataUpdate;


    private static Random rand = new Random();

    private final Collection<DashboardNotification> notifications = DummyDataGenerator
            .randomNotifications();

    /**
     * Initialize the data for this application.
     */






    /* JSON utility method */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /* JSON utility method */
    private static JsonObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is,
                    Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JsonElement jelement = new JsonParser().parse(jsonText);
            JsonObject jobject = jelement.getAsJsonObject();
            return jobject;
        } finally {
            is.close();
        }
    }

    /* JSON utility method */
    private static JsonObject readJsonFromFile(File path) throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(path));
        String jsonText = readAll(rd);
        JsonElement jelement = new JsonParser().parse(jsonText);
        JsonObject jobject = jelement.getAsJsonObject();
        return jobject;
    }



    /**
     * Create a list of dummy transactions
     *
     * @return
     */




    @Override
    public User authenticate(String userName, String password) {
        User user = new User();
        user.setFirstName(DummyDataGenerator.randomFirstName());
        user.setLastName(DummyDataGenerator.randomLastName());
        user.setRole("admin");
        String email = user.getFirstName().toLowerCase() + "."
                + user.getLastName().toLowerCase() + "@"
                + DummyDataGenerator.randomCompanyName().toLowerCase() + ".com";
        user.setEmail(email.replaceAll(" ", ""));
        user.setLocation(DummyDataGenerator.randomWord(5, true));
        user.setBio("Quis aute iure reprehenderit in voluptate velit esse."
                + "Cras mattis iudicium purus sit amet fermentum.");
        return user;
    }
    @Override
    public int getUnreadNotificationsCount() {
        Predicate<DashboardNotification> unreadPredicate = new Predicate<DashboardNotification>() {
            @Override
            public boolean apply(DashboardNotification input) {
                return !input.isRead();
            }
        };
        return Collections2.filter(notifications, unreadPredicate).size();
    }

    @Override
    public Collection<DashboardNotification> getNotifications() {
        for (DashboardNotification notification : notifications) {
            notification.setRead(true);
        }
        return Collections.unmodifiableCollection(notifications);
    }

    @Override
    public double getTotalSum() {

        return 0;
    }


}