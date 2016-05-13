package com.mygubbi.game.dashboard.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by nitinpuri on 13-05-2016.
 */
public class FileUtil {

    public String readFile(String path) {

        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        if (in != null) {
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
                String l;
                String val = "";
                while ((l = r.readLine()) != null) {
                    val = val + l;
                }

                return val;

            } catch (IOException e) {
                throw new RuntimeException("Error reading file: " + path, e);
            }
        } else {
            throw new RuntimeException("Error reading file: " + path);
        }

    }

}
