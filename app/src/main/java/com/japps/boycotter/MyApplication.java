package com.japps.boycotter;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {
    private static ExecutorService appProcessor;
    public static boolean activeInternet;

    public static final String BOYCOTT_PREFERENCE_KEY = "com.japps.boycotter.SCORE_PREFERENCE";
    public static final String BOYCOTT_SCORE_KEY = "com.japps.boycotter.BOYCOTTER_SCORE";
    public static int TOTAL_INSTALLED_CHINESE_APPS = 0;
    public static final String baseURL = "http://54.147.56.25:3000/appId/";

    public static final String[] chini_apps = {"TikTok", "LIKE", "Kwai", "Weibo", "WeChat", "SHAREit", "Truecaller", "UC News", "UCBrowser",
            "UCBrowser mini", "LiveMe", "Bigo Live", "Vigo Video", "BeautyPlus", "Xender", "Cam Scanner", "PUBG", "Clash of Kings",
            "Mobile Legends", "ClubFactory", "Shein", "Romwe", "AppLock", "Club Factory", "VMate", "Game of Sultans",
            "Mafia City", "Battle of Empires", "Vigo lite", "LivU", "Live Chat", "Guns of Glory", "Zak Zak Pro", "Turbo VPN",
            "Flash KeyBoard", "Hello Yo", "Dating.com", "NonoLive", "Game of Sultans", "UDictionary", "NewsDog", "VivaVideo", "Parallel Space",
            "APUS Browser", "Perfect Corp", "Cut Cut", "All video downloader", "Virus Cleaner",
            "CM Browser", "Mi Community", "DU recorder", "Vault-Hide",
            "YouCam Makeup", "Mi Store", "CacheClear", "DU Battery Saver",
            "DU Cleaner", "DU Privacy", "360 Security", "DU Browser", "Clean Master",
            "Baidu Translate", "Baidu Map", "Wonder Camera", "ES File Explorer", "Photo Wonder", "QQ International",
            "QQ Music", "QQ Mail", "QQ Player", "QQ NewsFeed", "WeSync", "QQ Security Centre", "SelfieCity",
            "Mail Master", "Mi Video", "call-Xiaomi", "QQ Launcher"};

    public static ExecutorService getExecutor() {
        if (appProcessor == null) {
            appProcessor = Executors.newCachedThreadPool();
            return appProcessor;
        }
        return appProcessor;
    }
}
