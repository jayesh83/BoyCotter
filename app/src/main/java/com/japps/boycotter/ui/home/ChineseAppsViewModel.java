package com.japps.boycotter.ui.home;

import android.graphics.drawable.Drawable;

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
    public static ChineseAppsViewModel getInstance(){
        if (viewModel == null){
            viewModel = new ChineseAppsViewModel();
            return viewModel;
        }
        return viewModel;
    }
}
