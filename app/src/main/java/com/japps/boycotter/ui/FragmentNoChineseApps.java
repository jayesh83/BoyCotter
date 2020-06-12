package com.japps.boycotter.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.japps.boycotter.R;

public class FragmentNoChineseApps extends Fragment {
    public FragmentNoChineseApps() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_chinese_apps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn_to_scoreBoard = view.findViewById(R.id.btn_goto_scoreBoard);
        btn_to_scoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                controller.navigate(FragmentNoChineseAppsDirections.actionFragmentNoChineseAppsToNavigationDashboard());
            }
        });
    }
}