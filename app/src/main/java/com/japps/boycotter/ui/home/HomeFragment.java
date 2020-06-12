package com.japps.boycotter.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.japps.boycotter.R;
import com.japps.boycotter.adapters.InstalledAppsListAdapter;

import java.util.ArrayList;

import static com.japps.boycotter.MyApplication.TOTAL_INSTALLED_CHINESE_APPS;

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

        if (TOTAL_INSTALLED_CHINESE_APPS == 0){
            ViewStub stub_no_apps = view.findViewById(R.id.viewstub_no_apps);
            stub_no_apps.inflate();
        }

        recyclerView = view.findViewById(R.id.boyCott_installed_apps_list);
        viewModel = ChineseAppsViewModel.getInstance();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

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
        NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        HomeFragmentDirections.ActionNavigationHomeToUninstaller2 directions = HomeFragmentDirections.actionNavigationHomeToUninstaller2(position);
        directions.setAppToUninstall(packageId);
        controller.navigate(directions);
    }

}