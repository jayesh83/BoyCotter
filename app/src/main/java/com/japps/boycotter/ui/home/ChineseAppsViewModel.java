package com.japps.boycotter.ui.home;

import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ChineseAppsViewModel extends ViewModel {
    private static ChineseAppsViewModel viewModel;
    private MutableLiveData<ArrayList<String>> installed_chinese_apps_name = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> installed_chinese_apps_package = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Drawable>> installed_chinese_apps_icons = new MutableLiveData<>();

    public LiveData<ArrayList<String>> getInstalled_chinese_apps_name() {
        return installed_chinese_apps_name;
    }

    public void setInstalled_chinese_apps_name(ArrayList<String> installed_chinese_apps_name) {
        this.installed_chinese_apps_name.postValue(installed_chinese_apps_name);
    }

    public LiveData<ArrayList<String>> getInstalled_chinese_apps_package() {
        return installed_chinese_apps_package;
    }

    public void setInstalled_chinese_apps_package(ArrayList<String> installed_chinese_apps_package) {
        this.installed_chinese_apps_package.postValue(installed_chinese_apps_package);
    }

    public MutableLiveData<ArrayList<Drawable>> getInstalled_chinese_apps_icons() {
        return installed_chinese_apps_icons;
    }

    public void setInstalled_chinese_apps_icons(ArrayList<Drawable> installed_chinese_apps_icons) {
        this.installed_chinese_apps_icons.postValue(installed_chinese_apps_icons);
    }

    public boolean removeApp(int appPos){
        String[] results = new String[2];
        Drawable icon = null;
        ArrayList<String> nameList = installed_chinese_apps_name.getValue();
        ArrayList<String> pkgList = installed_chinese_apps_package.getValue();
        ArrayList<Drawable> drawables = installed_chinese_apps_icons.getValue();

        if (nameList != null && pkgList != null && drawables != null){
            results[0] = nameList.remove(appPos);
            results[1] = pkgList.remove(appPos);
            icon = drawables.remove(appPos);
        }

        boolean isAppRemoved = results[0] != null && results[1] != null && icon != null;

        if (isAppRemoved){
            setInstalled_chinese_apps_name(nameList);
            setInstalled_chinese_apps_package(pkgList);
            setInstalled_chinese_apps_icons(drawables);
        }

        return isAppRemoved;
    }

    public static ChineseAppsViewModel getInstance(){
        if (viewModel == null){
            viewModel = new ChineseAppsViewModel();
            return viewModel;
        }
        return viewModel;
    }
}
