package com.japps.boycotter.ui.home;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.japps.boycotter.MyApplication;
import com.japps.boycotter.R;
import com.japps.boycotter.adapters.InstalledAppsListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.japps.boycotter.MyApplication.TOTAL_INSTALLED_CHINESE_APPS;
import static com.japps.boycotter.MyApplication.getExecutor;

public class HomeFragment extends Fragment implements InstalledAppsListAdapter.onUninstallClickListener {

    RecyclerView recyclerView;
    ChineseAppsViewModel viewModel;
    ArrayList<String> installed_chini_apps;
    ArrayList<String> installed_chini_apps_package;
    ArrayList<Drawable> installed_chini_apps_icon;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.boyCott_installed_apps_list);

        viewModel = ChineseAppsViewModel.getInstance();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> chini_apps = new ArrayList<>(Arrays.asList(MyApplication.chini_apps));

                ArrayList<String> chinese_installed_apps_names = new ArrayList<>();
                ArrayList<String> chinese_installed_apps_package_names = new ArrayList<>();
                ArrayList<Drawable> chinese_installed_apps_icons = new ArrayList<>();

                final PackageManager packageManager = requireContext().getPackageManager();

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
        });

        viewModel.getInstalled_chinese_apps_name().observe(requireActivity(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> apps) {
                installed_chini_apps = apps;
            }
        });

        viewModel.getInstalled_chinese_apps_package().observe(requireActivity(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> packages) {
                installed_chini_apps_package = packages;
            }
        });

        viewModel.getInstalled_chinese_apps_icons().observe(requireActivity(), new Observer<ArrayList<Drawable>>() {
            @Override
            public void onChanged(ArrayList<Drawable> icons) {
                installed_chini_apps_icon = icons;
                final InstalledAppsListAdapter adapter = new InstalledAppsListAdapter(installed_chini_apps, installed_chini_apps_icon, installed_chini_apps_package);
                recyclerView.setAdapter(adapter);
                adapter.setOnUninstallClickListener(HomeFragment.this);
            }
        });
    }

    @Override
    public void onUninstall(int position, String packageId) {
        // *** check in the hashMap if the packageId has some alternatives then pass it with the fragment arguments ****
        NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        HomeFragmentDirections.ActionNavigationHomeToUninstaller2 directions = HomeFragmentDirections.actionNavigationHomeToUninstaller2();
        String[] pkgs = {"sid.com.quotely", "com.whatsapp"};
        directions.setAppToUninstall(packageId);
        directions.setPackages(pkgs);
        controller.navigate(directions);
    }
    // TODO: a shared preference which keep the count of uninstalled apps
}