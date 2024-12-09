package com.example.p2pchatproject.util;

import com.example.p2pchatproject.HelloApplication;

import java.io.*;
import java.util.Properties;
import java.util.UUID;

public class Util {
    public static final String filename = "userdata.txt";

    public static String getProperty(String propertyName){
        Properties props = new Properties();
        try {
            props.load(HelloApplication.class.getResourceAsStream("configs/client.properties"));
            return props.getProperty(propertyName);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String[] getUserData(){
        String[] userData = new String[2]; // Userdata -> [String username, String uuid]
        try {
            userData = readUserData();
        } catch (NullPointerException | FileNotFoundException e){
            System.out.println(e.getMessage());
            userData[0] = "Client-1";
            userData[1] = UUID.randomUUID().toString();
            writeUserData(userData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Username: " + userData[0]);
            System.out.println("UUID: " + userData[1]);
        }
        return userData;
    }

    public static String[] readUserData() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(Util.filename))) {
            String[] userdata = new String[2]; // Userdata -> [String username, String uuid]
            String username;
            String uuid;
            if ((username = reader.readLine()) != null) {
                userdata[0] = username;
            } else {
                throw new NullPointerException("USERNAME not found");
            }
            if ((uuid = reader.readLine()) != null) {
                userdata[1] = uuid;
            } else {
                throw new NullPointerException("UUID not found");
            }
            return userdata;
        }
    }

    // Userdata -> [String username, String uuid]
    public static void writeUserData(String[] userdata){
        try (FileWriter writer = new FileWriter(Util.filename)){
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.println(userdata[0]);
            printWriter.println(userdata[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
