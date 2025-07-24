package com.lms.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    private static final Dotenv dotenv = Dotenv.configure()
                                               .ignoreIfMissing()
                                               .load();


    public static String get(String key) {
        return dotenv.get(key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }


}
