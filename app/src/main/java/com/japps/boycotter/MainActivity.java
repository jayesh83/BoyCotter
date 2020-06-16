package com.japps.boycotter;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.japps.boycotter.ui.home.ChineseAppsViewModel;
import com.japps.boycotter.util.InternetChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.japps.boycotter.MyApplication.TOTAL_INSTALLED_CHINESE_APPS;

public class MainActivity extends AppCompatActivity {
    BroadcastReceiver receiver;
    BottomNavigationView navView;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        categorise();

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        receiver = new InternetChecker();
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);

        navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_noChineseApps)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        changeStartDestination(TOTAL_INSTALLED_CHINESE_APPS);

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void changeStartDestination(int totalInstalledChineseApps) {

        NavInflater inflater = navController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.mobile_navigation);

        if (totalInstalledChineseApps == 0) {
            graph.setStartDestination(R.id.navigation_noChineseApps);

            Menu menu = navView.getMenu();
            menu.removeItem(R.id.navigation_home);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                menu.add(Menu.NONE, R.id.navigation_noChineseApps, 0, "Home").setIcon(getResources().getDrawable(R.drawable.ic_home_black_24dp, getTheme()));
            else
                menu.add(Menu.NONE, R.id.navigation_noChineseApps, 0, "Home").setIcon(getResources().getDrawable(R.drawable.ic_home_black_24dp));

        } else
            graph.setStartDestination(R.id.navigation_home);

        navController.setGraph(graph);
    }

    private void categorise() {
        ChineseAppsViewModel viewModel = ChineseAppsViewModel.getInstance();
        ArrayList<String> chini_apps = new ArrayList<>(Arrays.asList(MyApplication.chini_apps));

        ArrayList<String> chinese_installed_apps_names = new ArrayList<>();
        ArrayList<String> chinese_installed_apps_package_names = new ArrayList<>();
        ArrayList<Drawable> chinese_installed_apps_icons = new ArrayList<>();

        final PackageManager packageManager = getPackageManager();

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

        TOTAL_INSTALLED_CHINESE_APPS = chinese_installed_apps_names.size();
        viewModel.setInstalled_chinese_apps_name(chinese_installed_apps_names);
        viewModel.setInstalled_chinese_apps_package(chinese_installed_apps_package_names);
        viewModel.setInstalled_chinese_apps_icons(chinese_installed_apps_icons);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null)
            unregisterReceiver(receiver);
    }

}