package com.japps.boycotter.workers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.japps.boycotter.MyApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoriseChineseApps extends HandlerThread {
    Context context;
    Handler handler = null;
    public chineseAppsCallback callback;
    public CategoriseChineseApps(String name, Context context, chineseAppsCallback callback) {
        super(name);
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> chini_apps = new ArrayList<>(Arrays.asList(MyApplication.chini_apps));
                ArrayList<String> chinese_installed_apps_names = new ArrayList<>();
                ArrayList<String> chinese_installed_apps_package_names = new ArrayList<>();
                ArrayList<Drawable> chinese_installed_apps_icons = new ArrayList<>();
                Log.e("Setting Data -2", " : ");
                final PackageManager packageManager = context.getPackageManager();
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                final List<ResolveInfo> pkgAppsList = packageManager.queryIntentActivities(mainIntent, 0);

                for (int i = 0; i < pkgAppsList.size(); i++) {
                    ApplicationInfo applicationInfo = pkgAppsList.get(i).activityInfo.applicationInfo;

                    String package_name = applicationInfo.packageName;
                    String app_label = packageManager.getApplicationLabel(applicationInfo).toString();
                    Drawable app_icon = packageManager.getApplicationIcon(applicationInfo);

                    // if app is installed app
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                        if (chini_apps.contains(app_label)) {
                            chinese_installed_apps_names.add(app_label);
                            chinese_installed_apps_package_names.add(package_name);
                            chinese_installed_apps_icons.add(app_icon);
                        }
                }
                callback.appName(chinese_installed_apps_names);
                callback.appPackages(chinese_installed_apps_package_names);
                callback.appIcons(chinese_installed_apps_icons);
//                viewModel.setInstalled_chinese_apps_name(chinese_installed_apps_names);
//                viewModel.setInstalled_chinese_apps_package(chinese_installed_apps_package_names);
//                viewModel.setInstalled_chinese_apps_icons(chinese_installed_apps_icons);
                Log.e("Setting Data -3", " : ");
            }
        });
    }
    public interface chineseAppsCallback{
        public ArrayList<String> appName(ArrayList<String> appNames);
        public ArrayList<String> appPackages(ArrayList<String> packages);
        public ArrayList<Drawable> appIcons(ArrayList<Drawable> appIcons);
    }
}
